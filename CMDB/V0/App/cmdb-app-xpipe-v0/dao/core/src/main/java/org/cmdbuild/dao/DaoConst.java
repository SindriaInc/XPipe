package org.cmdbuild.dao;

import static org.cmdbuild.dao.constants.SystemAttributes.*;

public interface DaoConst {

    @Deprecated
    public enum SystemAttributes {
        Id(ATTR_ID),
        IdClass(ATTR_IDCLASS),
        ClassId1(ATTR_IDCLASS1),
        ClassId2(ATTR_IDCLASS2),
        CurrentId(ATTR_CURRENTID),
        DomainId(ATTR_IDDOMAIN),
        DomainId1(ATTR_IDOBJ1),
        DomainId2(ATTR_IDOBJ2),
        Code(ATTR_CODE),
        Description(ATTR_DESCRIPTION),
        Notes(ATTR_NOTES),
        BeginDate(ATTR_BEGINDATE),
        EndDate(ATTR_ENDDATE),
        User(ATTR_USER),
        Status(ATTR_STATUS),
        // dummy
        DomainQuerySource("_Src"), //
        DomainQueryTargetId("_DstId"), //
        RowNumber("_Row"), //
        RowsCount("_RowsCount"), //
        ;

        private final String dbName;

        SystemAttributes(String dbName) {
            this.dbName = dbName;
        }

        public String getDBName() {
            return dbName;
        }

    }

    static final String NULL = "NULL";

    static final String OPERATOR_EQ = "=";
    static final String OPERATOR_LT = "<";
    static final String OPERATOR_LT_EQ = "<=";
    static final String OPERATOR_GT = ">";
    static final String OPERATOR_GT_EQ = ">=";
    static final String OPERATOR_ILIKE = "ILIKE";
    static final String OPERATOR_NULL = "IS NULL";
    static final String OPERATOR_IN = "IN";

    static final String BASE_DOMAIN_TABLE_NAME = "Map";
    static final String DOMAIN_PREFIX = "Map_";
    static final String HISTORY_SUFFIX = "_history";

    static final String COMMENT_DESCR = "DESCR";
    static final String COMMENT_MODE = "MODE";
    static final String COMMENT_ACTIVE = "ACTIVE";

    static final String COMMENT_SUPERCLASS = "SUPERCLASS";
    static final String COMMENT_TYPE = "TYPE";
    static final String COMMENT_MULTITENANT_MODE = "MTMODE";

    static final String COMMENT_USERSTOPPABLE = "USERSTOPPABLE";
    static final String COMMENT_FLOW_STATUS_ATTR = "WFSTATUSATTR";
    static final String COMMENT_FLOW_SAVE_BUTTON_ENABLED = "WFSAVE";

    static final String COMMENT_TYPE_CLASS = "class";
    static final String COMMENT_TYPE_SIMPLECLASS = "simpleclass";
}
