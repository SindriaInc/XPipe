
package org.cmdbuild.services.soap.client.beans;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for menuSchema complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="menuSchema"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="children" type="{http://soap.services.cmdbuild.org}menuSchema" maxOccurs="unbounded" minOccurs="0" form="unqualified"/&gt;
 *         &lt;element name="classname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="unqualified"/&gt;
 *         &lt;element name="defaultToDisplay" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" form="unqualified"/&gt;
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="unqualified"/&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int" form="unqualified"/&gt;
 *         &lt;element name="menuType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="unqualified"/&gt;
 *         &lt;element name="metadata" type="{http://soap.services.cmdbuild.org}metadata" maxOccurs="unbounded" minOccurs="0" form="unqualified"/&gt;
 *         &lt;element name="parentId" type="{http://www.w3.org/2001/XMLSchema}int" form="unqualified"/&gt;
 *         &lt;element name="position" type="{http://www.w3.org/2001/XMLSchema}int" form="unqualified"/&gt;
 *         &lt;element name="privilege" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="unqualified"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "menuSchema", propOrder = {
    "children",
    "classname",
    "defaultToDisplay",
    "description",
    "id",
    "menuType",
    "metadata",
    "parentId",
    "position",
    "privilege"
})
public class MenuSchema {

    @XmlElement(namespace = "", nillable = true)
    protected List<MenuSchema> children;
    @XmlElement(namespace = "")
    protected String classname;
    @XmlElement(namespace = "")
    protected Boolean defaultToDisplay;
    @XmlElement(namespace = "")
    protected String description;
    @XmlElement(namespace = "")
    protected long id;
    @XmlElement(namespace = "")
    protected String menuType;
    @XmlElement(namespace = "", nillable = true)
    protected List<Metadata> metadata;
    @XmlElement(namespace = "")
    protected long parentId;
    @XmlElement(namespace = "")
    protected int position;
    @XmlElement(namespace = "")
    protected String privilege;

    /**
     * Gets the value of the children property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the children property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChildren().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MenuSchema }
     * 
     * 
     */
    public List<MenuSchema> getChildren() {
        if (children == null) {
            children = new ArrayList<MenuSchema>();
        }
        return this.children;
    }

    /**
     * Gets the value of the classname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassname() {
        return classname;
    }

    /**
     * Sets the value of the classname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassname(String value) {
        this.classname = value;
    }

    /**
     * Gets the value of the defaultToDisplay property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDefaultToDisplay() {
        return defaultToDisplay;
    }

    /**
     * Sets the value of the defaultToDisplay property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDefaultToDisplay(Boolean value) {
        this.defaultToDisplay = value;
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
     * Gets the value of the menuType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMenuType() {
        return menuType;
    }

    /**
     * Sets the value of the menuType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMenuType(String value) {
        this.menuType = value;
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
     * Gets the value of the parentId property.
     * 
     */
    public long getParentId() {
        return parentId;
    }

    /**
     * Sets the value of the parentId property.
     * 
     */
    public void setParentId(long value) {
        this.parentId = value;
    }

    /**
     * Gets the value of the position property.
     * 
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the value of the position property.
     * 
     */
    public void setPosition(int value) {
        this.position = value;
    }

    /**
     * Gets the value of the privilege property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrivilege() {
        return privilege;
    }

    /**
     * Sets the value of the privilege property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrivilege(String value) {
        this.privilege = value;
    }

}
