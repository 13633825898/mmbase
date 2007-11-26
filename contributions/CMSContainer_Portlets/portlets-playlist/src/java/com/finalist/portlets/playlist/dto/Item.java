//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.09.11 at 05:02:54 PM CEST 
//

package com.finalist.portlets.playlist.dto;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for item element declaration.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;element name=&quot;item&quot;&gt;
 *   &lt;complexType&gt;
 *     &lt;complexContent&gt;
 *       &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *         &lt;sequence&gt;
 *           &lt;element ref=&quot;{}title&quot;/&gt;
 *           &lt;element ref=&quot;{}artist&quot;/&gt;
 *           &lt;element ref=&quot;{}dalet_reference&quot;/&gt;
 *           &lt;element ref=&quot;{}tracknr&quot;/&gt;
 *           &lt;element ref=&quot;{}carrier_code&quot;/&gt;
 *         &lt;/sequence&gt;
 *         &lt;attribute name=&quot;played_ms&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}integer&quot; /&gt;
 *         &lt;attribute name=&quot;starttime&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}anySimpleType&quot; /&gt;
 *       &lt;/restriction&gt;
 *     &lt;/complexContent&gt;
 *   &lt;/complexType&gt;
 * &lt;/element&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "title", "artist", "daletReference", "tracknr", "carrierCode" })
@XmlRootElement(name = "item")
public class Item {

   @XmlElement(required = true)
   protected String title;
   @XmlElement(required = true)
   protected String artist;
   @XmlElement(name = "dalet_reference", required = true)
   protected BigInteger daletReference;
   @XmlElement(required = true)
   protected BigInteger tracknr;
   @XmlElement(name = "carrier_code", required = true)
   protected CarrierCode carrierCode;
   @XmlAttribute(name = "played_ms", required = true)
   protected BigInteger playedMs;
   @XmlAttribute(required = true)
   protected String starttime;


   /**
    * Gets the value of the title property.
    * 
    * @return possible object is {@link String }
    */
   public String getTitle() {
      return title;
   }


   /**
    * Sets the value of the title property.
    * 
    * @param value
    *           allowed object is {@link String }
    */
   public void setTitle(String value) {
      this.title = value;
   }


   /**
    * Gets the value of the artist property.
    * 
    * @return possible object is {@link String }
    */
   public String getArtist() {
      return artist;
   }


   /**
    * Sets the value of the artist property.
    * 
    * @param value
    *           allowed object is {@link String }
    */
   public void setArtist(String value) {
      this.artist = value;
   }


   /**
    * Gets the value of the daletReference property.
    * 
    * @return possible object is {@link BigInteger }
    */
   public BigInteger getDaletReference() {
      return daletReference;
   }


   /**
    * Sets the value of the daletReference property.
    * 
    * @param value
    *           allowed object is {@link BigInteger }
    */
   public void setDaletReference(BigInteger value) {
      this.daletReference = value;
   }


   /**
    * Gets the value of the tracknr property.
    * 
    * @return possible object is {@link BigInteger }
    */
   public BigInteger getTracknr() {
      return tracknr;
   }


   /**
    * Sets the value of the tracknr property.
    * 
    * @param value
    *           allowed object is {@link BigInteger }
    */
   public void setTracknr(BigInteger value) {
      this.tracknr = value;
   }


   /**
    * Gets the value of the carrierCode property.
    * 
    * @return possible object is {@link CarrierCode }
    */
   public CarrierCode getCarrierCode() {
      return carrierCode;
   }


   /**
    * Sets the value of the carrierCode property.
    * 
    * @param value
    *           allowed object is {@link CarrierCode }
    */
   public void setCarrierCode(CarrierCode value) {
      this.carrierCode = value;
   }


   /**
    * Gets the value of the playedMs property.
    * 
    * @return possible object is {@link BigInteger }
    */
   public BigInteger getPlayedMs() {
      return playedMs;
   }


   /**
    * Sets the value of the playedMs property.
    * 
    * @param value
    *           allowed object is {@link BigInteger }
    */
   public void setPlayedMs(BigInteger value) {
      this.playedMs = value;
   }


   /**
    * Gets the value of the starttime property.
    * 
    * @return possible object is {@link String }
    */
   public String getStarttime() {
      return starttime;
   }


   /**
    * Sets the value of the starttime property.
    * 
    * @param value
    *           allowed object is {@link String }
    */
   public void setStarttime(String value) {
      this.starttime = value;
   }

}
