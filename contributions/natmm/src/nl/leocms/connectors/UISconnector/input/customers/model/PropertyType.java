//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.5-b16-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.07.27 at 04:59:29 PM MSD 
//


package nl.leocms.connectors.UISconnector.input.customers.model;


/**
 * Java content class for anonymous complex type.
 * <p>The following schema fragment specifies the expected content contained within this java content object. (defined at file:/D:/JAVA/WSDP/16/jaxb/bin/Untitled3.xsd line 76)
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="propertyId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="propertyDescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{}propertyValue" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public interface PropertyType {


    /**
     * Gets the value of the propertyDescription property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String}
     */
    java.lang.String getPropertyDescription();

    /**
     * Sets the value of the propertyDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    void setPropertyDescription(java.lang.String value);

    /**
     * Gets the value of the PropertyValue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the PropertyValue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPropertyValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link nl.leocms.connectors.UISconnector.input.customers.model.PropertyValueType}
     * {@link nl.leocms.connectors.UISconnector.input.customers.model.PropertyValue}
     * 
     */
    java.util.List getPropertyValue();

    /**
     * Gets the value of the propertyId property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String}
     */
    java.lang.String getPropertyId();

    /**
     * Sets the value of the propertyId property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    void setPropertyId(java.lang.String value);

}
