package org.cmdbuild.services.soap.client.beans;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "card", propOrder = {
	"attributeList",
	"beginDate",
	"className",
	"endDate",
	"id",
	"metadata",
	"user"
})
@XmlSeeAlso({
	CardExt.class
})
public class Card {

	@XmlElement(nillable = true)
	protected List<Attribute> attributeList;
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar beginDate;
	protected String className;
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar endDate;
	protected long id;
	@XmlElement(nillable = true)
	protected List<Metadata> metadata;
	protected String user;

	/**
	 * Gets the value of the attributeList property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the attributeList property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getAttributeList().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link Attribute }
	 * 
	 * 
	 */
	public List<Attribute> getAttributeList() {
		if (attributeList == null) {
			attributeList = new ArrayList<Attribute>();
		}
		return this.attributeList;
	}

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
	 * Gets the value of the className property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Sets the value of the className property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setClassName(String value) {
		this.className = value;
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
	 * Gets the value of the id property.
	 * 
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the value of the id property.
	 * 
	 */
	public void setId(long value) {
		this.id = value;
	}

	/**
	 * Gets the value of the metadata property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the metadata property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getMetadata().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link Metadata }
	 * 
	 * 
	 */
	public List<Metadata> getMetadata() {
		if (metadata == null) {
			metadata = new ArrayList<Metadata>();
		}
		return this.metadata;
	}

	/**
	 * Gets the value of the user property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Sets the value of the user property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setUser(String value) {
		this.user = value;
	}

}
