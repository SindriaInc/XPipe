package org.cmdbuild.workflow.xpdl;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;
import java.util.ArrayList;
import java.util.List;

import net.jcip.annotations.NotThreadSafe;
import org.cmdbuild.common.annotations.Legacy;
import org.cmdbuild.dao.entrytype.Attribute;

import org.cmdbuild.workflow.type.LookupType;
import org.cmdbuild.workflow.type.ReferenceType;
import org.enhydra.jxpdl.XMLInterface;
import org.enhydra.jxpdl.XMLInterfaceImpl;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.DataField;
import org.enhydra.jxpdl.elements.DataTypes;
import org.enhydra.jxpdl.elements.Package;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.TypeDeclaration;
import org.enhydra.jxpdl.elements.TypeDeclarations;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.enhydra.jxpdl.elements.WorkflowProcesses;
import org.cmdbuild.workflow.model.WorkflowConstants;
import static org.cmdbuild.workflow.xpdl.SharkXpdlUtils.getStandardPackageId;
import static org.cmdbuild.workflow.xpdl.SharkXpdlUtils.getStandardProcessDefinitionId;
import org.cmdbuild.workflow.model.Process;

@NotThreadSafe
public class XpdlDocumentHelper {

    private static final String GRAMMAR_JAVA = "text/java";
    private static final String GRAMMAR_JAVA_SCRIPT = "text/javascript";
    private static final String GRAMMAR_PYTHON_SCRIPT = "text/pythonscript";

    private static final String DEFAULT_XPDL_VERSION = "2.1";

    public static final String ARRAY_DECLARED_TYPE_NAME_SUFFIX = WorkflowConstants.XPDL_ARRAY_DECLARED_TYPE_SUFFIX;
    public static final String ARRAY_DECLARED_TYPE_LOCATION_SUFFIX = "<>";

    private static final String DEFAULT_SYSTEM_PARTICIPANT = "System";

    private final Package pkg;
    private final XMLInterface xmlInterface;

    public XpdlDocumentHelper(String pkgId) {
        this(new Package());
        pkg.setId(pkgId);
        pkg.getPackageHeader().setXPDLVersion(DEFAULT_XPDL_VERSION);
    }

    public XpdlDocumentHelper(Package pkg) {
        xmlInterface = new XMLInterfaceImpl();
        this.pkg = checkNotNull(pkg);
    }

    public static XpdlDocumentHelper createXpdlTemplateForProcess(Process process, List<String> groupNamesForRoleParticipant) {
        XpdlDocumentHelper doc = new XpdlDocumentHelper(getStandardPackageId(process));
        doc.createCustomTypeDeclarations();
        doc.setDefaultScriptingLanguage(ScriptLanguage.JAVA);
        addProcessWithFields(doc, process);
        doc.addSystemParticipant(DEFAULT_SYSTEM_PARTICIPANT);
        groupNamesForRoleParticipant.forEach((name) -> {
            doc.addRoleParticipant(name);
        });
        return doc;
    }

    private static void addProcessWithFields(XpdlDocumentHelper doc, Process process) {
        String procDefId = getStandardProcessDefinitionId(process);
        XpdlProcess proc = doc.createProcess(procDefId);
        addBindedClass(doc, process);
        DaoToXpdlAttributeTypeConverter typeConverter = new DaoToXpdlAttributeTypeConverter();
        for (Attribute a : process.getServiceAttributes()) {
            XpdlDocumentHelper.StandardAndCustomTypes type = typeConverter.convertType(a.getType());
            if (type != null) {
                proc.addField(a.getName(), type);
            }
        }
    }

    @Legacy("As in 1.x")
    private static void addBindedClass(XpdlDocumentHelper doc, Process process) {
        doc.findProcess(getStandardProcessDefinitionId(process)).setBindToClass(process.getName());
    }

    public Package getPkg() {
        return pkg;
    }

    public String getPackageId() {
        return pkg.getId();
    }

    public XpdlProcess createProcess(String procDefId) {
        turnReadWrite();
        WorkflowProcess wp = (WorkflowProcess) pkg.getWorkflowProcesses().generateNewElement();
        wp.setId(procDefId);
        pkg.getWorkflowProcesses().add(wp);
        return new XpdlProcess(this, wp);
    }

    public XpdlProcess findProcess(String procDefId) {
        WorkflowProcess wp = pkg.getWorkflowProcess(procDefId);
        if (wp != null) {
            return new XpdlProcess(this, wp);
        } else {
            return null;
        }
    }

    public List<XpdlProcess> findAllProcesses() {
        WorkflowProcesses wps = pkg.getWorkflowProcesses();
        List<XpdlProcess> out = new ArrayList<>(wps.size());
        for (int i = 0; i < wps.size(); ++i) {
            WorkflowProcess wp = (WorkflowProcess) wps.get(i);
            out.add(new XpdlProcess(this, wp));
        }
        return out;
    }

    public void addPackageField(String dfId, StandardAndCustomTypes type) {
        turnReadWrite();
        DataField df = createDataField(dfId, type);
        pkg.getDataFields().add(df);
    }

    DataField createDataField(String dfId, StandardAndCustomTypes type) {
        DataField df = (DataField) pkg.getDataFields().generateNewElement();
        df.setId(dfId);
        type.setTypeToField(df);
        return df;
    }

    public void setDefaultScriptingLanguage(ScriptLanguage lang) {
        pkg.getScript().setType(lang.mimeType);
    }

    public void addRoleParticipant(String participantId) {
        turnReadWrite();
        Participant p = (Participant) pkg.getParticipants().generateNewElement();
        p.setId(participantId);
        // Default but better safe than sorry
        p.getParticipantType().setTypeROLE();
        pkg.getParticipants().add(p);
    }

    public boolean hasRoleParticipant(String participantId) {
        Participant p = pkg.getParticipants().getParticipant(participantId);
        return (p != null) && (XPDLConstants.PARTICIPANT_TYPE_ROLE.equals(p.getParticipantType().getType()));
    }

    public void addSystemParticipant(String participantId) {
        turnReadWrite();
        Participant p = (Participant) pkg.getParticipants().generateNewElement();
        p.setId(participantId);
        p.getParticipantType().setTypeSYSTEM();
        pkg.getParticipants().add(p);
    }

    /*
	 * For backward compatibility
     */
    public void createCustomTypeDeclarations() {
        TypeDeclarations types = pkg.getTypeDeclarations();
        for (StandardAndCustomTypes t : StandardAndCustomTypes.values()) {
            if (t.isCustom()) {
                addExternalReferenceType(types, t);
//                addExternalReferenceArrayType(types, t);
            }
        }
    }

    private void addExternalReferenceType(TypeDeclarations types, StandardAndCustomTypes t) {
//        addExternalReferenceType(types, t.getDeclaredTypeId(), t.getDeclaredTypeLocation());
        String location = t.getDeclaredTypeLocation();
        switch (t) {
            case LOOKUPARRAY:
            case REFERENCEARRAY:
                location += ARRAY_DECLARED_TYPE_LOCATION_SUFFIX;//TODO check this, improve
        }

        turnReadWrite();
        TypeDeclaration type = (TypeDeclaration) types.generateNewElement();
        type.setId(t.getDeclaredTypeId());
        type.getDataTypes().getExternalReference().setLocation(location);
        type.getDataTypes().setExternalReference();
        types.add(type);
    }

//    private void addExternalReferenceArrayType(TypeDeclarations types, StandardAndCustomTypes t) {
//        addExternalReferenceType(types, t.getDeclaredTypeId() + ARRAY_DECLARED_TYPE_NAME_SUFFIX,
//                t.getDeclaredTypeLocation() + ARRAY_DECLARED_TYPE_LOCATION_SUFFIX);
//    }
//    private void addExternalReferenceType(TypeDeclarations types, String id, String location) {
//        turnReadWrite();
//        TypeDeclaration type = (TypeDeclaration) types.generateNewElement();
//        type.setId(id);
//        type.getDataTypes().getExternalReference().setLocation(location);
//        type.getDataTypes().setExternalReference();
//        types.add(type);
//    }
    /**
     * Aberration because the library does not allow graph traversal when in
     * read/write mode. This function should be called before querying the graph
     * unless elements are accessed by id.
     *
     * We are more interested in development speed than running speed, so we put
     * the whole package in read only.
     */
    void turnReadOnly() {
        if (!pkg.isReadOnly()) {
            pkg.setReadOnly(true);
            pkg.initCaches(xmlInterface);
        }
    }

    /**
     * Aberration because the library rejects changes to the tree when in read
     * only mode. This function should be called before every "add" operation.
     *
     * We are more interested in development speed than running speed, so we put
     * the whole package in read write.
     */
    void turnReadWrite() {
        if (pkg.isReadOnly()) {
            pkg.setReadOnly(false);
        }
    }

    public enum ScriptLanguage {
        JAVA(GRAMMAR_JAVA),
        JAVASCRIPT(GRAMMAR_JAVA_SCRIPT),
        PYTHON(GRAMMAR_PYTHON_SCRIPT),
        GROOVY("text/groovy");

        private final String mimeType;

        private ScriptLanguage(String mimeType) {
            this.mimeType = mimeType;
        }

        public String getMimeType() {
            return mimeType;
        }

        public static ScriptLanguage of(String mimeType) {
            for (ScriptLanguage language : values()) {
                if (language.mimeType.equals(mimeType)) {
                    return language;
                }
            }
            throw new IllegalArgumentException("invalid mime-type");
        }

    }

    public enum StandardAndCustomTypes {
        BOOLEAN {
            @Override
            protected void selectDataType(DataTypes dataTypes) {
                dataTypes.getBasicType().setTypeBOOLEAN();
            }
        },
        DATETIME {
            @Override
            protected void selectDataType(DataTypes dataTypes) {
                dataTypes.getBasicType().setTypeDATETIME();
            }
        },
        FLOAT {
            @Override
            protected void selectDataType(DataTypes dataTypes) {
                dataTypes.getBasicType().setTypeFLOAT();
            }
        },
        INTEGER {
            @Override
            protected void selectDataType(DataTypes dataTypes) {
                dataTypes.getBasicType().setTypeINTEGER();
            }
        },
        STRING {
            @Override
            protected void selectDataType(DataTypes dataTypes) {
                dataTypes.getBasicType().setTypeSTRING();
            }
        },
        /*
		 * For backward compatibility
         */
        REFERENCE(WorkflowConstants.XPDL_REFERENCE_DECLARED_TYPE, ReferenceType.class.getName()) {
            @Override
            protected void selectDataType(DataTypes dataTypes) {
                dataTypes.setDeclaredType();
                dataTypes.getDeclaredType().setId(getDeclaredTypeId());
            }
        },
        LOOKUP(WorkflowConstants.XPDL_LOOKUP_DECLARED_TYPE, LookupType.class.getName()) {
            @Override
            protected void selectDataType(DataTypes dataTypes) {
                dataTypes.setDeclaredType();
                dataTypes.getDeclaredType().setId(getDeclaredTypeId());
            }
        },
        LOOKUPARRAY(WorkflowConstants.XPDL_LOOKUP_ARRAY_DECLARED_TYPE, LookupType.class.getName()) {
            @Override
            protected void selectDataType(DataTypes dataTypes) {
//                dataTypes.setArrayType();  
                dataTypes.setDeclaredType();
                dataTypes.getDeclaredType().setId(getDeclaredTypeId());
            }
        },
        REFERENCEARRAY(WorkflowConstants.XPDL_REFERENCE_ARRAY_DECLARED_TYPE, ReferenceType.class.getName()) {
            @Override
            protected void selectDataType(DataTypes dataTypes) {
                dataTypes.setDeclaredType();
                dataTypes.getDeclaredType().setId(getDeclaredTypeId());
            }
        },;

        private final String declaredTypeId;
        private final String declaredTypeLocation;

        private StandardAndCustomTypes() {
            this.declaredTypeId = null;
            this.declaredTypeLocation = null;
        }

        private StandardAndCustomTypes(String declaredTypeId, String declaredTypeLocation) {
            this.declaredTypeId = declaredTypeId;
            this.declaredTypeLocation = declaredTypeLocation;
        }

        public boolean isCustom() {
            return (declaredTypeId != null);
        }

        public String getDeclaredTypeId() {
            return declaredTypeId;
        }

        public String getDeclaredTypeLocation() {
            return declaredTypeLocation;
        }

        public void setTypeToField(DataField df) {
            selectDataType(df.getDataType().getDataTypes());
        }

        abstract protected void selectDataType(DataTypes dataTypes);
    }

}
