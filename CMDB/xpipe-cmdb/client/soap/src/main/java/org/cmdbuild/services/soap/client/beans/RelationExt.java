
package org.cmdbuild.services.soap.client.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for relationExt complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="relationExt"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://soap.services.cmdbuild.org}relation"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="card1Code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="card1Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="card2Code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="card2Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "relationExt", propOrder = {
    "card1Code",
    "card1Description",
    "card2Code",
    "card2Description"
})
public class RelationExt
    extends Relation
{

    protected String card1Code;
    protected String card1Description;
    protected String card2Code;
    protected String card2Description;

    /**
     * Gets the value of the card1Code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCard1Code() {
        return card1Code;
    }

    /**
     * Sets the value of the card1Code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCard1Code(String value) {
        this.card1Code = value;
    }

    /**
     * Gets the value of the card1Description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCard1Description() {
        return card1Description;
    }

    /**
     * Sets the value of the card1Description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCard1Description(String value) {
        this.card1Description = value;
    }

    /**
     * Gets the value of the card2Code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCard2Code() {
        return card2Code;
    }

    /**
     * Sets the value of the card2Code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCard2Code(String value) {
        this.card2Code = value;
    }

    /**
     * Gets the value of the card2Description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCard2Description() {
        return card2Description;
    }

    /**
     * Sets the value of the card2Description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCard2Description(String value) {
        this.card2Description = value;
    }

}
