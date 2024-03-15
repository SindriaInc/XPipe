
package org.cmdbuild.services.soap.client.beans;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cqlQuery complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cqlQuery"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cqlQuery" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="parameters" type="{http://soap.services.cmdbuild.org}cqlParameter" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cqlQuery", propOrder = {
    "cqlQuery",
    "parameters"
})
public class CqlQuery {

    protected String cqlQuery;
    @XmlElement(nillable = true)
    protected List<CqlParameter> parameters;

    /**
     * Gets the value of the cqlQuery property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCqlQuery() {
        return cqlQuery;
    }

    /**
     * Sets the value of the cqlQuery property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCqlQuery(String value) {
        this.cqlQuery = value;
    }

    /**
     * Gets the value of the parameters property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameters property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameters().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CqlParameter }
     * 
     * 
     */
    public List<CqlParameter> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<CqlParameter>();
        }
        return this.parameters;
    }

}
