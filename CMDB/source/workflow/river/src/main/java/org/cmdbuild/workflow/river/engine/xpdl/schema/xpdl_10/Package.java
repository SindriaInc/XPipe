//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.04.02 at 07:43:50 AM CEST 
//
package org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_10;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Java class for anonymous complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}PackageHeader"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}RedefinableHeader" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}ConformanceClass" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Script" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}ExternalPackages" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}TypeDeclarations" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Participants" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Applications" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}DataFields" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}WorkflowProcesses" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}ExtendedAttributes" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Id" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "packageHeader",
    "redefinableHeader",
    "conformanceClass",
    "script",
    "externalPackages",
    "typeDeclarations",
    "participants",
    "applications",
    "dataFields",
    "workflowProcesses",
    "extendedAttributes"
})
@XmlRootElement(name = "Package")
public class Package {

    @XmlElement(name = "PackageHeader", required = true)
    protected PackageHeader packageHeader;
    @XmlElement(name = "RedefinableHeader")
    protected RedefinableHeader redefinableHeader;
    @XmlElement(name = "ConformanceClass")
    protected ConformanceClass conformanceClass;
    @XmlElement(name = "Script")
    protected Script script;
    @XmlElement(name = "ExternalPackages")
    protected ExternalPackages externalPackages;
    @XmlElement(name = "TypeDeclarations")
    protected TypeDeclarations typeDeclarations;
    @XmlElement(name = "Participants")
    protected Participants participants;
    @XmlElement(name = "Applications")
    protected Applications applications;
    @XmlElement(name = "DataFields")
    protected DataFields dataFields;
    @XmlElement(name = "WorkflowProcesses")
    protected WorkflowProcesses workflowProcesses;
    @XmlElement(name = "ExtendedAttributes")
    protected ExtendedAttributes extendedAttributes;
    @XmlAttribute(name = "Id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String id;
    @XmlAttribute(name = "Name")
    protected String name;

    /**
     * Gets the value of the packageHeader property.
     *
     * @return possible object is {@link PackageHeader }
     *
     */
    public PackageHeader getPackageHeader() {
        return packageHeader;
    }

    /**
     * Sets the value of the packageHeader property.
     *
     * @param value allowed object is {@link PackageHeader }
     *
     */
    public void setPackageHeader(PackageHeader value) {
        this.packageHeader = value;
    }

    /**
     * Gets the value of the redefinableHeader property.
     *
     * @return possible object is {@link RedefinableHeader }
     *
     */
    public RedefinableHeader getRedefinableHeader() {
        return redefinableHeader;
    }

    /**
     * Sets the value of the redefinableHeader property.
     *
     * @param value allowed object is {@link RedefinableHeader }
     *
     */
    public void setRedefinableHeader(RedefinableHeader value) {
        this.redefinableHeader = value;
    }

    /**
     * Gets the value of the conformanceClass property.
     *
     * @return possible object is {@link ConformanceClass }
     *
     */
    public ConformanceClass getConformanceClass() {
        return conformanceClass;
    }

    /**
     * Sets the value of the conformanceClass property.
     *
     * @param value allowed object is {@link ConformanceClass }
     *
     */
    public void setConformanceClass(ConformanceClass value) {
        this.conformanceClass = value;
    }

    /**
     * Gets the value of the script property.
     *
     * @return possible object is {@link Script }
     *
     */
    public Script getScript() {
        return script;
    }

    /**
     * Sets the value of the script property.
     *
     * @param value allowed object is {@link Script }
     *
     */
    public void setScript(Script value) {
        this.script = value;
    }

    /**
     * Gets the value of the externalPackages property.
     *
     * @return possible object is {@link ExternalPackages }
     *
     */
    public ExternalPackages getExternalPackages() {
        return externalPackages;
    }

    /**
     * Sets the value of the externalPackages property.
     *
     * @param value allowed object is {@link ExternalPackages }
     *
     */
    public void setExternalPackages(ExternalPackages value) {
        this.externalPackages = value;
    }

    /**
     * Gets the value of the typeDeclarations property.
     *
     * @return possible object is {@link TypeDeclarations }
     *
     */
    public TypeDeclarations getTypeDeclarations() {
        return typeDeclarations;
    }

    /**
     * Sets the value of the typeDeclarations property.
     *
     * @param value allowed object is {@link TypeDeclarations }
     *
     */
    public void setTypeDeclarations(TypeDeclarations value) {
        this.typeDeclarations = value;
    }

    /**
     * Gets the value of the participants property.
     *
     * @return possible object is {@link Participants }
     *
     */
    public Participants getParticipants() {
        return participants;
    }

    /**
     * Sets the value of the participants property.
     *
     * @param value allowed object is {@link Participants }
     *
     */
    public void setParticipants(Participants value) {
        this.participants = value;
    }

    /**
     * Gets the value of the applications property.
     *
     * @return possible object is {@link Applications }
     *
     */
    public Applications getApplications() {
        return applications;
    }

    /**
     * Sets the value of the applications property.
     *
     * @param value allowed object is {@link Applications }
     *
     */
    public void setApplications(Applications value) {
        this.applications = value;
    }

    /**
     * Gets the value of the dataFields property.
     *
     * @return possible object is {@link DataFields }
     *
     */
    public DataFields getDataFields() {
        return dataFields;
    }

    /**
     * Sets the value of the dataFields property.
     *
     * @param value allowed object is {@link DataFields }
     *
     */
    public void setDataFields(DataFields value) {
        this.dataFields = value;
    }

    /**
     * Gets the value of the workflowProcesses property.
     *
     * @return possible object is {@link WorkflowProcesses }
     *
     */
    public WorkflowProcesses getWorkflowProcesses() {
        return workflowProcesses;
    }

    /**
     * Sets the value of the workflowProcesses property.
     *
     * @param value allowed object is {@link WorkflowProcesses }
     *
     */
    public void setWorkflowProcesses(WorkflowProcesses value) {
        this.workflowProcesses = value;
    }

    /**
     * Gets the value of the extendedAttributes property.
     *
     * @return possible object is {@link ExtendedAttributes }
     *
     */
    public ExtendedAttributes getExtendedAttributes() {
        return extendedAttributes;
    }

    /**
     * Sets the value of the extendedAttributes property.
     *
     * @param value allowed object is {@link ExtendedAttributes }
     *
     */
    public void setExtendedAttributes(ExtendedAttributes value) {
        this.extendedAttributes = value;
    }

    /**
     * Gets the value of the id property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

}
