
package org.cmdbuild.services.soap.client.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AbstractWorkflowEvent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractWorkflowEvent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://soap.services.cmdbuild.org}AbstractEvent"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="processDefinitionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="processInstanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="sessionId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractWorkflowEvent", propOrder = {
    "processDefinitionId",
    "processInstanceId",
    "sessionId"
})
@XmlSeeAlso({
    ProcessStartEvent.class,
    ProcessUpdateEvent.class
})
public abstract class AbstractWorkflowEvent
    extends AbstractEvent
{

    protected String processDefinitionId;
    protected String processInstanceId;
    protected int sessionId;

    /**
     * Gets the value of the processDefinitionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    /**
     * Sets the value of the processDefinitionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessDefinitionId(String value) {
        this.processDefinitionId = value;
    }

    /**
     * Gets the value of the processInstanceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    /**
     * Sets the value of the processInstanceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessInstanceId(String value) {
        this.processInstanceId = value;
    }

    /**
     * Gets the value of the sessionId property.
     * 
     */
    public int getSessionId() {
        return sessionId;
    }

    /**
     * Sets the value of the sessionId property.
     * 
     */
    public void setSessionId(int value) {
        this.sessionId = value;
    }

}
