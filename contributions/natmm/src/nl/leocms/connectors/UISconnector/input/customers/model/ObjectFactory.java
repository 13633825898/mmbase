//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.5-b16-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.07.27 at 04:59:29 PM MSD 
//


package nl.leocms.connectors.UISconnector.input.customers.model;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the nl.leocms.connectors.UISconnector.input.customers.model package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
public class ObjectFactory
    extends nl.leocms.connectors.UISconnector.input.customers.model.impl.runtime.DefaultJAXBContextImpl
{

    private static java.util.HashMap defaultImplementations = new java.util.HashMap(23, 0.75F);
    private static java.util.HashMap rootTagMap = new java.util.HashMap();
    public final static nl.leocms.connectors.UISconnector.input.customers.model.impl.runtime.GrammarInfo grammarInfo = new nl.leocms.connectors.UISconnector.input.customers.model.impl.runtime.GrammarInfoImpl(rootTagMap, defaultImplementations, (nl.leocms.connectors.UISconnector.input.customers.model.ObjectFactory.class));
    public final static java.lang.Class version = (nl.leocms.connectors.UISconnector.input.customers.model.impl.JAXBVersion.class);

    static {
        defaultImplementations.put((nl.leocms.connectors.UISconnector.input.customers.model.PersonalInformationType.class), "nl.leocms.connectors.UISconnector.input.customers.model.impl.PersonalInformationTypeImpl");
        defaultImplementations.put((nl.leocms.connectors.UISconnector.input.customers.model.PropertyValueType.class), "nl.leocms.connectors.UISconnector.input.customers.model.impl.PropertyValueTypeImpl");
        defaultImplementations.put((nl.leocms.connectors.UISconnector.input.customers.model.PropertyValue.class), "nl.leocms.connectors.UISconnector.input.customers.model.impl.PropertyValueImpl");
        defaultImplementations.put((nl.leocms.connectors.UISconnector.input.customers.model.PropertyList.class), "nl.leocms.connectors.UISconnector.input.customers.model.impl.PropertyListImpl");
        defaultImplementations.put((nl.leocms.connectors.UISconnector.input.customers.model.PropertyType.class), "nl.leocms.connectors.UISconnector.input.customers.model.impl.PropertyTypeImpl");
        defaultImplementations.put((nl.leocms.connectors.UISconnector.input.customers.model.BusinessInformationType.class), "nl.leocms.connectors.UISconnector.input.customers.model.impl.BusinessInformationTypeImpl");
        defaultImplementations.put((nl.leocms.connectors.UISconnector.input.customers.model.BusinessInformation.class), "nl.leocms.connectors.UISconnector.input.customers.model.impl.BusinessInformationImpl");
        defaultImplementations.put((nl.leocms.connectors.UISconnector.input.customers.model.CustomerInformationType.class), "nl.leocms.connectors.UISconnector.input.customers.model.impl.CustomerInformationTypeImpl");
        defaultImplementations.put((nl.leocms.connectors.UISconnector.input.customers.model.PersonalInformation.class), "nl.leocms.connectors.UISconnector.input.customers.model.impl.PersonalInformationImpl");
        defaultImplementations.put((nl.leocms.connectors.UISconnector.input.customers.model.CommonInformation.class), "nl.leocms.connectors.UISconnector.input.customers.model.impl.CommonInformationImpl");
        defaultImplementations.put((nl.leocms.connectors.UISconnector.input.customers.model.Address.class), "nl.leocms.connectors.UISconnector.input.customers.model.impl.AddressImpl");
        defaultImplementations.put((nl.leocms.connectors.UISconnector.input.customers.model.AddressType.class), "nl.leocms.connectors.UISconnector.input.customers.model.impl.AddressTypeImpl");
        defaultImplementations.put((nl.leocms.connectors.UISconnector.input.customers.model.Property.class), "nl.leocms.connectors.UISconnector.input.customers.model.impl.PropertyImpl");
        defaultImplementations.put((nl.leocms.connectors.UISconnector.input.customers.model.CommonInformationType.class), "nl.leocms.connectors.UISconnector.input.customers.model.impl.CommonInformationTypeImpl");
        defaultImplementations.put((nl.leocms.connectors.UISconnector.input.customers.model.CustomerInformation.class), "nl.leocms.connectors.UISconnector.input.customers.model.impl.CustomerInformationImpl");
        defaultImplementations.put((nl.leocms.connectors.UISconnector.input.customers.model.PropertyListType.class), "nl.leocms.connectors.UISconnector.input.customers.model.impl.PropertyListTypeImpl");
        rootTagMap.put(new javax.xml.namespace.QName("", "businessInformation"), (nl.leocms.connectors.UISconnector.input.customers.model.BusinessInformation.class));
        rootTagMap.put(new javax.xml.namespace.QName("", "propertyValue"), (nl.leocms.connectors.UISconnector.input.customers.model.PropertyValue.class));
        rootTagMap.put(new javax.xml.namespace.QName("", "property"), (nl.leocms.connectors.UISconnector.input.customers.model.Property.class));
        rootTagMap.put(new javax.xml.namespace.QName("", "address"), (nl.leocms.connectors.UISconnector.input.customers.model.Address.class));
        rootTagMap.put(new javax.xml.namespace.QName("", "customerInformation"), (nl.leocms.connectors.UISconnector.input.customers.model.CustomerInformation.class));
        rootTagMap.put(new javax.xml.namespace.QName("", "propertyList"), (nl.leocms.connectors.UISconnector.input.customers.model.PropertyList.class));
        rootTagMap.put(new javax.xml.namespace.QName("", "commonInformation"), (nl.leocms.connectors.UISconnector.input.customers.model.CommonInformation.class));
        rootTagMap.put(new javax.xml.namespace.QName("", "personalInformation"), (nl.leocms.connectors.UISconnector.input.customers.model.PersonalInformation.class));
    }

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: nl.leocms.connectors.UISconnector.input.customers.model
     * 
     */
    public ObjectFactory() {
        super(grammarInfo);
    }

    /**
     * Create an instance of the specified Java content interface.
     * 
     * @param javaContentInterface
     *     the Class object of the javacontent interface to instantiate
     * @return
     *     a new instance
     * @throws JAXBException
     *     if an error occurs
     */
    public java.lang.Object newInstance(java.lang.Class javaContentInterface)
        throws javax.xml.bind.JAXBException
    {
        return super.newInstance(javaContentInterface);
    }

    /**
     * Get the specified property. This method can only be
     * used to get provider specific properties.
     * Attempting to get an undefined property will result
     * in a PropertyException being thrown.
     * 
     * @param name
     *     the name of the property to retrieve
     * @return
     *     the value of the requested property
     * @throws PropertyException
     *     when there is an error retrieving the given property or value
     */
    public java.lang.Object getProperty(java.lang.String name)
        throws javax.xml.bind.PropertyException
    {
        return super.getProperty(name);
    }

    /**
     * Set the specified property. This method can only be
     * used to set provider specific properties.
     * Attempting to set an undefined property will result
     * in a PropertyException being thrown.
     * 
     * @param value
     *     the value of the property to be set
     * @param name
     *     the name of the property to retrieve
     * @throws PropertyException
     *     when there is an error processing the given property or value
     */
    public void setProperty(java.lang.String name, java.lang.Object value)
        throws javax.xml.bind.PropertyException
    {
        super.setProperty(name, value);
    }

    /**
     * Create an instance of PersonalInformationType
     * 
     * @throws JAXBException
     *     if an error occurs
     */
    public nl.leocms.connectors.UISconnector.input.customers.model.PersonalInformationType createPersonalInformationType()
        throws javax.xml.bind.JAXBException
    {
        return new nl.leocms.connectors.UISconnector.input.customers.model.impl.PersonalInformationTypeImpl();
    }

    /**
     * Create an instance of PropertyValueType
     * 
     * @throws JAXBException
     *     if an error occurs
     */
    public nl.leocms.connectors.UISconnector.input.customers.model.PropertyValueType createPropertyValueType()
        throws javax.xml.bind.JAXBException
    {
        return new nl.leocms.connectors.UISconnector.input.customers.model.impl.PropertyValueTypeImpl();
    }

    /**
     * Create an instance of PropertyValue
     * 
     * @throws JAXBException
     *     if an error occurs
     */
    public nl.leocms.connectors.UISconnector.input.customers.model.PropertyValue createPropertyValue()
        throws javax.xml.bind.JAXBException
    {
        return new nl.leocms.connectors.UISconnector.input.customers.model.impl.PropertyValueImpl();
    }

    /**
     * Create an instance of PropertyList
     * 
     * @throws JAXBException
     *     if an error occurs
     */
    public nl.leocms.connectors.UISconnector.input.customers.model.PropertyList createPropertyList()
        throws javax.xml.bind.JAXBException
    {
        return new nl.leocms.connectors.UISconnector.input.customers.model.impl.PropertyListImpl();
    }

    /**
     * Create an instance of PropertyType
     * 
     * @throws JAXBException
     *     if an error occurs
     */
    public nl.leocms.connectors.UISconnector.input.customers.model.PropertyType createPropertyType()
        throws javax.xml.bind.JAXBException
    {
        return new nl.leocms.connectors.UISconnector.input.customers.model.impl.PropertyTypeImpl();
    }

    /**
     * Create an instance of BusinessInformationType
     * 
     * @throws JAXBException
     *     if an error occurs
     */
    public nl.leocms.connectors.UISconnector.input.customers.model.BusinessInformationType createBusinessInformationType()
        throws javax.xml.bind.JAXBException
    {
        return new nl.leocms.connectors.UISconnector.input.customers.model.impl.BusinessInformationTypeImpl();
    }

    /**
     * Create an instance of BusinessInformation
     * 
     * @throws JAXBException
     *     if an error occurs
     */
    public nl.leocms.connectors.UISconnector.input.customers.model.BusinessInformation createBusinessInformation()
        throws javax.xml.bind.JAXBException
    {
        return new nl.leocms.connectors.UISconnector.input.customers.model.impl.BusinessInformationImpl();
    }

    /**
     * Create an instance of CustomerInformationType
     * 
     * @throws JAXBException
     *     if an error occurs
     */
    public nl.leocms.connectors.UISconnector.input.customers.model.CustomerInformationType createCustomerInformationType()
        throws javax.xml.bind.JAXBException
    {
        return new nl.leocms.connectors.UISconnector.input.customers.model.impl.CustomerInformationTypeImpl();
    }

    /**
     * Create an instance of PersonalInformation
     * 
     * @throws JAXBException
     *     if an error occurs
     */
    public nl.leocms.connectors.UISconnector.input.customers.model.PersonalInformation createPersonalInformation()
        throws javax.xml.bind.JAXBException
    {
        return new nl.leocms.connectors.UISconnector.input.customers.model.impl.PersonalInformationImpl();
    }

    /**
     * Create an instance of CommonInformation
     * 
     * @throws JAXBException
     *     if an error occurs
     */
    public nl.leocms.connectors.UISconnector.input.customers.model.CommonInformation createCommonInformation()
        throws javax.xml.bind.JAXBException
    {
        return new nl.leocms.connectors.UISconnector.input.customers.model.impl.CommonInformationImpl();
    }

    /**
     * Create an instance of Address
     * 
     * @throws JAXBException
     *     if an error occurs
     */
    public nl.leocms.connectors.UISconnector.input.customers.model.Address createAddress()
        throws javax.xml.bind.JAXBException
    {
        return new nl.leocms.connectors.UISconnector.input.customers.model.impl.AddressImpl();
    }

    /**
     * Create an instance of AddressType
     * 
     * @throws JAXBException
     *     if an error occurs
     */
    public nl.leocms.connectors.UISconnector.input.customers.model.AddressType createAddressType()
        throws javax.xml.bind.JAXBException
    {
        return new nl.leocms.connectors.UISconnector.input.customers.model.impl.AddressTypeImpl();
    }

    /**
     * Create an instance of Property
     * 
     * @throws JAXBException
     *     if an error occurs
     */
    public nl.leocms.connectors.UISconnector.input.customers.model.Property createProperty()
        throws javax.xml.bind.JAXBException
    {
        return new nl.leocms.connectors.UISconnector.input.customers.model.impl.PropertyImpl();
    }

    /**
     * Create an instance of CommonInformationType
     * 
     * @throws JAXBException
     *     if an error occurs
     */
    public nl.leocms.connectors.UISconnector.input.customers.model.CommonInformationType createCommonInformationType()
        throws javax.xml.bind.JAXBException
    {
        return new nl.leocms.connectors.UISconnector.input.customers.model.impl.CommonInformationTypeImpl();
    }

    /**
     * Create an instance of CustomerInformation
     * 
     * @throws JAXBException
     *     if an error occurs
     */
    public nl.leocms.connectors.UISconnector.input.customers.model.CustomerInformation createCustomerInformation()
        throws javax.xml.bind.JAXBException
    {
        return new nl.leocms.connectors.UISconnector.input.customers.model.impl.CustomerInformationImpl();
    }

    /**
     * Create an instance of PropertyListType
     * 
     * @throws JAXBException
     *     if an error occurs
     */
    public nl.leocms.connectors.UISconnector.input.customers.model.PropertyListType createPropertyListType()
        throws javax.xml.bind.JAXBException
    {
        return new nl.leocms.connectors.UISconnector.input.customers.model.impl.PropertyListTypeImpl();
    }

}
