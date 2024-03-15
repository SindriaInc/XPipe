
package org.cmdbuild.services.soap.client.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getClassSchemaById complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getClassSchemaById"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="classId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="includeAttributes" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getClassSchemaById", propOrder = {
    "classId",
    "includeAttributes"
})
public class GetClassSchemaById {

    protected long classId;
    protected boolean includeAttributes;

    /**
     * Gets the value of the classId property.
     * 
     */
    public long getClassId() {
        return classId;
    }

    /**
     * Sets the value of the classId property.
     * 
     */
    public void setClassId(long value) {
        this.classId = value;
    }

    /**
     * Gets the value of the includeAttributes property.
     * 
     */
    public boolean isIncludeAttributes() {
        return includeAttributes;
    }

    /**
     * Sets the value of the includeAttributes property.
     * 
     */
    public void setIncludeAttributes(boolean value) {
        this.includeAttributes = value;
    }

}
