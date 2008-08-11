package org.mmbase.applications.vprowizards.spring.action;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.mmbase.applications.vprowizards.spring.FieldError;
import org.mmbase.applications.vprowizards.spring.GlobalError;
import org.mmbase.applications.vprowizards.spring.ResultContainer;
import org.mmbase.applications.vprowizards.spring.cache.CacheFlushHint;
import org.mmbase.applications.vprowizards.spring.util.DateTime;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Transaction;
import org.mmbase.util.logging.Logging;
import org.springframework.web.multipart.MultipartFile;

/**
 * this is a template class for the 'real' node actions
 * 
 * @author ebunders
 * 
 */
public abstract class AbstractNodeAction extends Action {

	private Map<String, String> fields = new HashMap<String, String>();
	private Map<String, DateTime> dateFields = new HashMap<String, DateTime>();
	private String id = null;
	private MultipartFile file = null;
	private static org.mmbase.util.logging.Logger log = Logging.getLoggerInstance(AbstractNodeAction.class);

	private Node node;

	private boolean nodeChanged = false;
	

	private ResultContainer resultContainer;

	/**
	 * This final method calls the different template methods, that are either abstract or have a default
	 * implementation.
	 * <ul>
	 * <li>{@link #getNode(Transaction, HttpServletRequest)} is called to obtain a node instance
	 * <li>{@link #shouldProcess(Node)} is called to determine if the operation should proceed (if this depends on some
	 * specific criterium.)
	 * <li>{@link #processNode(Node, ResultContainer)} is called after the default fields are set (fields collection,
	 * datefields collection and file field).
	 * <li>{@link #createCacheFlushHints()} is called to return the appropriate cacheflush hints. Use
	 * {@link #hasChanged()} to find out if the current node has been updated. Use
	 * {@link #addCachFlushHint(CacheFlushHint)} to add hints
	 * </ul>
	 * When the id is set on this action, the node is added to the idmap.
	 */
	@Override
	public final void process(Map<String, Node> nodeMap, ResultContainer resultContainer) {
		this.resultContainer = resultContainer;
		node = createNode(resultContainer.getTransaction(), nodeMap, resultContainer.getRequest());
		
		if (resultContainer.containsGlobalErrors()) {
			return;
		}
		if (node == null) {
			throw new IllegalStateException(
					"No node has been provided, and no error has been set. Either of these should happen");
		}
		if (shouldProcess(node)) {
			setBasicFields();
			if (resultContainer.containsFieldErrors()) {
				return;
			}
			if (getId() != null) {
				resultContainer.getIdMap().put(getId(), node);
				log.debug("node ["+node+"] is registered under id " + getId());
			}
			processNode(resultContainer.getTransaction());
			createCacheFlushHints();
		}
	}

	/*
	 * these are the template methods
	 */

	/**
	 * This template method is called to obtain the node for this action. it is the responsibility of the implementation
	 * of this method to set a global error (using addGlobalError) when something goes wrong. In that case the action
	 * stops.
	 * 
	 * @param transaction
	 *            the mmmbase transaction
	 * @return the node this action should handle or null if an error occurs
	 * @param request
	 * 
	 */
	abstract protected Node createNode(Transaction transaction, Map<String,Node> idMap, HttpServletRequest request);

	/**
	 * This template method is called after the values that have been injected in this action in the fields and
	 * datefields collections, as well as the file field have been set on the node. The default implementation does
	 * nothing
	 * 
	 * @param node
	 * @param resultContainer
	 */
	protected void processNode(Transaction transaction) {};

	/**
	 * this template method is called before any changes are made to the node to edit. if it returns false, no more
	 * action wil be taken. Use this if you want to use some kind of switch.
	 * 
	 * @param node
	 *            the current node.
	 * @return true if there is no reason not to process this node
	 */
	protected boolean shouldProcess(Node node) {
		return true;
	}
	
	protected final Locale getLocale(){
		return resultContainer.getLocale();
	}
	
	protected final Node getNode(){
		return node;
	}

	/**
	 * call this method to set the changed flag on this node
	 */
	protected final void setChanged() {
		nodeChanged = true;
	}

	protected final boolean hasChanged() {
		return nodeChanged;
	}

	
	/**
	 * Creates a field error for this action
	 * @param field
	 * @param key
	 * @param placeholderValues
	 */
	protected final void addFieldError(String field, String key, String[] placeholderValues) {
		resultContainer.getFieldErrors().add(new FieldError(field, key, placeholderValues, getLocale()));
	}
	
	/**
	 * Creates a field error for this action
	 * @param field
	 * @param key
	 */
	protected final void addFieldError(String field, String key) {
		resultContainer.getFieldErrors().add(new FieldError(field, key, getLocale()));
	}

	
	/**
	 * Creates a global error for this action.
	 * @param key
	 * @param placeholderValues
	 */
	protected final void addGlobalError(String key, String[] placeholderValues) {
		resultContainer.getGlobalErrors().add(new GlobalError(key, placeholderValues, getLocale()));
	}
	
	/**
	 * Creates a global error for this action.
	 * @param key
	 */
	protected final void addGlobalError(String key) {
		resultContainer.getGlobalErrors().add(new GlobalError(key, getLocale()));
	}

	protected final void addCachFlushHint(CacheFlushHint hint) {
		resultContainer.addCacheFlushHint(hint);
	}

	/**
	 * This template method is called when the node to edit is changed. custom cachflush hints can be set here. This
	 * class dous not know if the current node is updated or created, so it dousn't know what kind of events to create.
	 * 
	 */
	protected abstract void createCacheFlushHints();

	/**
	 * Set the fields, dateFields and file on the given node. Errors are created when fields are not part of the present
	 * nodes manager. TODO: use datatypes to validate the input. TODO: a builder can have more than one binary field
	 * with another name than 'handle'
	 */
	private final void setBasicFields() {
		NodeManager nm = node.getNodeManager();
		for (String field : fields.keySet()) {
			if (!nm.hasField(field)) {
				addGlobalError("error.field.unknown", new String[] { field, this.getClass().getName(), nm.getName() });
			}
			if (!node.getStringValue(field).equals(fields.get(field))) {
				setChanged();
			}
			node.setStringValue(field, fields.get(field));
		}

		for (String field : dateFields.keySet()) {
			try {
				if (!nm.hasField(field)) {
					addGlobalError("error.field.unknown", new String[] { field, this.getClass().getName(), nm.getName() });
				}
				if (dateFields.get(field).getDateInSeconds() != node.getIntValue(field)) {
					node.setDateValue(field, dateFields.get(field).getParsedDate());
					setChanged();
				}
			} catch (ParseException e) {
				addFieldError(field, e.getMessage());
			}
		}

		// set the file field
		if (getFile() != null && nm.getField("handle") == null) {
			addGlobalError("error.field.unknown", new String[] { "handle", this.getClass().getName(), nm.getName() });
		} else {
			setHandlerField();
		}

	}

	/**
	 * This method sets the handle field of a given node if there is a file upload inside the current action. It also
	 * tries to set a number of other fields on the node (if they exist):<br>
	 * <ul>
	 * <li>filename : the original file name (not the path)</li>
	 * <li>filesize </li>
	 * <li>size </li>
	 * <li>mimetype </li>
	 * </ul>
	 * Current limitations are:
	 * <ul>
	 * <li>you can only set one file for an action
	 * <li>the field name (handle) is hardcoded.
	 * <ul>
	 * 
	 */
	private final void setHandlerField() {
		boolean changed = false;
		try {
			MultipartFile file = getFile();
			if (getFile() != null && !getFile().isEmpty()) {
				NodeManager nodeManager = node.getNodeManager();

				node.setByteValue("handle", file.getBytes());
				changed = true;

				// see if we can set a derived filename
				if (nodeManager.hasField("filename")) {

					String fileName = file.getOriginalFilename();
					int pos1 = fileName.lastIndexOf("/");
					int pos2 = fileName.lastIndexOf("\\");
					int pos = pos1 > pos2 ? pos1 : pos2;
					if (pos > 0) {
						fileName = fileName.substring(pos + 1);
						if ("".equals(fileName)) {
							fileName = file.getOriginalFilename();
						}
					}
					node.setStringValue("filename", fileName);
				}

				// see if we can set the mimetype
				if (nodeManager.hasField("mimetype")) {
					String mimetype = file.getContentType();
					node.setStringValue("mimetype", mimetype);
				}

				// the file size
				long filesize = file.getSize();
				if (nodeManager.hasField("filesize")) {
					node.setLongValue("filesize", filesize);
				}
				if (nodeManager.hasField("size")) {
					node.setLongValue("size", filesize);
				}

			}
		} catch (IOException e) {
			addFieldError("file", "" + e);
		}
		if (changed) {
			setChanged();
		}
	}

	/*
	 * these are the setter methods for the data binding.
	 */

	public void setDateFields(Map<String, DateTime> dateFields) {
		this.dateFields = dateFields;
	}

	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public void setId(String id) {
		this.id = id;
	}

	public MultipartFile getFile() {
		return file;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this).toString();
	}

}
