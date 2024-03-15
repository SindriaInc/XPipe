
package org.cmdbuild.services.soap.client.beans;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for activitySchema complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="activitySchema"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="attributes" type="{http://soap.services.cmdbuild.org}attributeSchema" maxOccurs="unbounded" minOccurs="0" form="unqualified"/&gt;
 *         &lt;element name="widgets" type="{http://soap.services.cmdbuild.org}workflowWidgetDefinition" maxOccurs="unbounded" minOccurs="0" form="unqualified"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "activitySchema", propOrder = {
    "attributes",
    "widgets"
})
public class ActivitySchema {

    @XmlElement(namespace = "", nillable = true)
    protected List<AttributeSchema> attributes;
    @XmlElement(namespace = "", nillable = true)
    protected List<WorkflowWidgetDefinition> widgets;

    /**
     * Gets the value of the attributes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attributes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttributes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AttributeSchema }
     * 
     * 
     */
    public List<AttributeSchema> getAttributes() {
        if (attributes == null) {
            attributes = new ArrayList<AttributeSchema>();
        }
        return this.attributes;
    }

    /**
     * Gets the value of the widgets property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the widgets property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWidgets().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WorkflowWidgetDefinition }
     * 
     * 
     */
    public List<WorkflowWidgetDefinition> getWidgets() {
        if (widgets == null) {
            widgets = new ArrayList<WorkflowWidgetDefinition>();
        }
        return this.widgets;
    }

}
