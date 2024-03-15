
package org.cmdbuild.services.soap.client.beans;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for attributeSchema complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="attributeSchema"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="baseDSP" type="{http://www.w3.org/2001/XMLSchema}boolean" form="unqualified"/&gt;
 *         &lt;element name="classorder" type="{http://www.w3.org/2001/XMLSchema}int" form="unqualified"/&gt;
 *         &lt;element name="defaultValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="unqualified"/&gt;
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="unqualified"/&gt;
 *         &lt;element name="domainName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="unqualified"/&gt;
 *         &lt;element name="fieldmode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="unqualified"/&gt;
 *         &lt;element name="idClass" type="{http://www.w3.org/2001/XMLSchema}int" form="unqualified"/&gt;
 *         &lt;element name="idDomain" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0" form="unqualified"/&gt;
 *         &lt;element name="index" type="{http://www.w3.org/2001/XMLSchema}int" form="unqualified"/&gt;
 *         &lt;element name="inherited" type="{http://www.w3.org/2001/XMLSchema}boolean" form="unqualified"/&gt;
 *         &lt;element name="length" type="{http://www.w3.org/2001/XMLSchema}int" form="unqualified"/&gt;
 *         &lt;element name="lookupType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="unqualified"/&gt;
 *         &lt;element name="metadata" type="{http://soap.services.cmdbuild.org}metadata" maxOccurs="unbounded" minOccurs="0" form="unqualified"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="unqualified"/&gt;
 *         &lt;element name="notnull" type="{http://www.w3.org/2001/XMLSchema}boolean" form="unqualified"/&gt;
 *         &lt;element name="precision" type="{http://www.w3.org/2001/XMLSchema}int" form="unqualified"/&gt;
 *         &lt;element name="referencedClassName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="unqualified"/&gt;
 *         &lt;element name="referencedIdClass" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0" form="unqualified"/&gt;
 *         &lt;element name="scale" type="{http://www.w3.org/2001/XMLSchema}int" form="unqualified"/&gt;
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="unqualified"/&gt;
 *         &lt;element name="unique" type="{http://www.w3.org/2001/XMLSchema}boolean" form="unqualified"/&gt;
 *         &lt;element name="visibility" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="unqualified"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "attributeSchema", propOrder = {
    "baseDSP",
    "classorder",
    "defaultValue",
    "description",
    "domainName",
    "fieldmode",
    "idClass",
    "idDomain",
    "index",
    "inherited",
    "length",
    "lookupType",
    "metadata",
    "name",
    "notnull",
    "precision",
    "referencedClassName",
    "referencedIdClass",
    "scale",
    "type",
    "unique",
    "visibility"
})
public class AttributeSchema {

    @XmlElement(namespace = "")
    protected boolean baseDSP;
    @XmlElement(namespace = "")
    protected int classorder;
    @XmlElement(namespace = "")
    protected String defaultValue;
    @XmlElement(namespace = "")
    protected String description;
    @XmlElement(namespace = "")
    protected String domainName;
    @XmlElement(namespace = "")
    protected String fieldmode;
    @XmlElement(namespace = "")
    protected int idClass;
    @XmlElement(namespace = "")
    protected Long idDomain;
    @XmlElement(namespace = "")
    protected int index;
    @XmlElement(namespace = "")
    protected boolean inherited;
    @XmlElement(namespace = "")
    protected int length;
    @XmlElement(namespace = "")
    protected String lookupType;
    @XmlElement(namespace = "", nillable = true)
    protected List<Metadata> metadata;
    @XmlElement(namespace = "")
    protected String name;
    @XmlElement(namespace = "")
    protected boolean notnull;
    @XmlElement(namespace = "")
    protected int precision;
    @XmlElement(namespace = "")
    protected String referencedClassName;
    @XmlElement(namespace = "")
    protected Long referencedIdClass;
    @XmlElement(namespace = "")
    protected int scale;
    @XmlElement(namespace = "")
    protected String type;
    @XmlElement(namespace = "")
    protected boolean unique;
    @XmlElement(namespace = "")
    protected String visibility;

    /**
     * Gets the value of the baseDSP property.
     * 
     */
    public boolean isBaseDSP() {
        return baseDSP;
    }

    /**
     * Sets the value of the baseDSP property.
     * 
     */
    public void setBaseDSP(boolean value) {
        this.baseDSP = value;
    }

    /**
     * Gets the value of the classorder property.
     * 
     */
    public int getClassorder() {
        return classorder;
    }

    /**
     * Sets the value of the classorder property.
     * 
     */
    public void setClassorder(int value) {
        this.classorder = value;
    }

    /**
     * Gets the value of the defaultValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the value of the defaultValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
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
     * Gets the value of the fieldmode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFieldmode() {
        return fieldmode;
    }

    /**
     * Sets the value of the fieldmode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFieldmode(String value) {
        this.fieldmode = value;
    }

    /**
     * Gets the value of the idClass property.
     * 
     */
    public int getIdClass() {
        return idClass;
    }

    /**
     * Sets the value of the idClass property.
     * 
     */
    public void setIdClass(int value) {
        this.idClass = value;
    }

    /**
     * Gets the value of the idDomain property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdDomain() {
        return idDomain;
    }

    /**
     * Sets the value of the idDomain property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdDomain(Long value) {
        this.idDomain = value;
    }

    /**
     * Gets the value of the index property.
     * 
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the value of the index property.
     * 
     */
    public void setIndex(int value) {
        this.index = value;
    }

    /**
     * Gets the value of the inherited property.
     * 
     */
    public boolean isInherited() {
        return inherited;
    }

    /**
     * Sets the value of the inherited property.
     * 
     */
    public void setInherited(boolean value) {
        this.inherited = value;
    }

    /**
     * Gets the value of the length property.
     * 
     */
    public int getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     */
    public void setLength(int value) {
        this.length = value;
    }

    /**
     * Gets the value of the lookupType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLookupType() {
        return lookupType;
    }

    /**
     * Sets the value of the lookupType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLookupType(String value) {
        this.lookupType = value;
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
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the notnull property.
     * 
     */
    public boolean isNotnull() {
        return notnull;
    }

    /**
     * Sets the value of the notnull property.
     * 
     */
    public void setNotnull(boolean value) {
        this.notnull = value;
    }

    /**
     * Gets the value of the precision property.
     * 
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * Sets the value of the precision property.
     * 
     */
    public void setPrecision(int value) {
        this.precision = value;
    }

    /**
     * Gets the value of the referencedClassName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferencedClassName() {
        return referencedClassName;
    }

    /**
     * Sets the value of the referencedClassName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferencedClassName(String value) {
        this.referencedClassName = value;
    }

    /**
     * Gets the value of the referencedIdClass property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getReferencedIdClass() {
        return referencedIdClass;
    }

    /**
     * Sets the value of the referencedIdClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setReferencedIdClass(Long value) {
        this.referencedIdClass = value;
    }

    /**
     * Gets the value of the scale property.
     * 
     */
    public int getScale() {
        return scale;
    }

    /**
     * Sets the value of the scale property.
     * 
     */
    public void setScale(int value) {
        this.scale = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the unique property.
     * 
     */
    public boolean isUnique() {
        return unique;
    }

    /**
     * Sets the value of the unique property.
     * 
     */
    public void setUnique(boolean value) {
        this.unique = value;
    }

    /**
     * Gets the value of the visibility property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * Sets the value of the visibility property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVisibility(String value) {
        this.visibility = value;
    }

}
