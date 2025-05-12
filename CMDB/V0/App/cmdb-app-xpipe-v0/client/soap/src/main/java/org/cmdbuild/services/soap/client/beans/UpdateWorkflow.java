
package org.cmdbuild.services.soap.client.beans;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for updateWorkflow complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updateWorkflow"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="card" type="{http://soap.services.cmdbuild.org}card" minOccurs="0"/&gt;
 *         &lt;element name="completeTask" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="widgets" type="{http://soap.services.cmdbuild.org}workflowWidgetSubmission" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateWorkflow", propOrder = {
    "card",
    "completeTask",
    "widgets"
})
public class UpdateWorkflow {

    protected Card card;
    protected boolean completeTask;
    protected List<WorkflowWidgetSubmission> widgets;

    /**
     * Gets the value of the card property.
     * 
     * @return
     *     possible object is
     *     {@link Card }
     *     
     */
    public Card getCard() {
        return card;
    }

    /**
     * Sets the value of the card property.
     * 
     * @param value
     *     allowed object is
     *     {@link Card }
     *     
     */
    public void setCard(Card value) {
        this.card = value;
    }

    /**
     * Gets the value of the completeTask property.
     * 
     */
    public boolean isCompleteTask() {
        return completeTask;
    }

    /**
     * Sets the value of the completeTask property.
     * 
     */
    public void setCompleteTask(boolean value) {
        this.completeTask = value;
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
     * {@link WorkflowWidgetSubmission }
     * 
     * 
     */
    public List<WorkflowWidgetSubmission> getWidgets() {
        if (widgets == null) {
            widgets = new ArrayList<WorkflowWidgetSubmission>();
        }
        return this.widgets;
    }

}
