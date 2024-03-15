
package org.cmdbuild.services.soap.client.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for workflow complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="workflow"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="processid" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="processinstanceid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "workflow", propOrder = {
    "processid",
    "processinstanceid"
})
public class Workflow {

    protected long processid;
    protected String processinstanceid;

    /**
     * Gets the value of the processid property.
     * 
     */
    public long getProcessid() {
        return processid;
    }

    /**
     * Sets the value of the processid property.
     * 
     */
    public void setProcessid(long value) {
        this.processid = value;
    }

    /**
     * Gets the value of the processinstanceid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessinstanceid() {
        return processinstanceid;
    }

    /**
     * Sets the value of the processinstanceid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessinstanceid(String value) {
        this.processinstanceid = value;
    }

}
