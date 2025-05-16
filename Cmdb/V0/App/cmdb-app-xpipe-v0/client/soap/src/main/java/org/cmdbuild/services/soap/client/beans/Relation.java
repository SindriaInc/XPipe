
package org.cmdbuild.services.soap.client.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for relation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="relation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="beginDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="card1Id" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="card2Id" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="class1Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="class2Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="domainName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "relation", propOrder = {
    "beginDate",
    "card1Id",
    "card2Id",
    "class1Name",
    "class2Name",
    "domainName",
    "endDate",
    "status"
})
@XmlSeeAlso({
    RelationExt.class
})
public class Relation {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar beginDate;
    protected long card1Id;
    protected long card2Id;
    protected String class1Name;
    protected String class2Name;
    protected String domainName;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endDate;
    protected String status;

    /**
     * Gets the value of the beginDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBeginDate() {
        return beginDate;
    }

    /**
     * Sets the value of the beginDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBeginDate(XMLGregorianCalendar value) {
        this.beginDate = value;
    }

    /**
     * Gets the value of the card1Id property.
     * 
     */
    public long getCard1Id() {
        return card1Id;
    }

    /**
     * Sets the value of the card1Id property.
     * 
     */
    public void setCard1Id(long value) {
        this.card1Id = value;
    }

    /**
     * Gets the value of the card2Id property.
     * 
     */
    public long getCard2Id() {
        return card2Id;
    }

    /**
     * Sets the value of the card2Id property.
     * 
     */
    public void setCard2Id(long value) {
        this.card2Id = value;
    }

    /**
     * Gets the value of the class1Name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClass1Name() {
        return class1Name;
    }

    /**
     * Sets the value of the class1Name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClass1Name(String value) {
        this.class1Name = value;
    }

    /**
     * Gets the value of the class2Name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClass2Name() {
        return class2Name;
    }

    /**
     * Sets the value of the class2Name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClass2Name(String value) {
        this.class2Name = value;
    }

    /**
     * Gets the value of the domainName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomainName() {
        return domainName;
    }

    /**
     * Sets the value of the domainName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomainName(String value) {
        this.domainName = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndDate(XMLGregorianCalendar value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

}
