/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_CLASS_NAME;
import org.cmdbuild.dao.orm.annotations.CardMapping;

import static java.util.Collections.emptyMap;
import java.util.EnumSet;
import java.util.Map;
import javax.annotation.Nullable;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_PRIVILEGED_CLASS_ID;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_PRIVILEGED_OBJECT_ID;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_MODE;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_PRIVILEGE_FILTER;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_UI_CARD_EDIT_MODE;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_ATTRIBUTES_PRIVILEGES;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_DMS_PRIVILEGES;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_GIS_PRIVILEGES;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_PRIVILEGED_OBJECT_CODE;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_TYPE;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_ROLE_ID;
import static org.cmdbuild.auth.grant.GrantMode.GM_NONE;
import static org.cmdbuild.auth.grant.GrantMode.GM_READ;
import static org.cmdbuild.auth.grant.GrantMode.GM_WF_ADMIN;
import static org.cmdbuild.auth.grant.GrantMode.GM_WF_BASIC;
import static org.cmdbuild.auth.grant.GrantMode.GM_WF_DEFAULT;
import static org.cmdbuild.auth.grant.GrantMode.GM_WF_PLUS;
import static org.cmdbuild.auth.grant.GrantMode.GM_WRITE;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_CLASS;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_PROCESS;
import org.cmdbuild.auth.role.Role;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNullOrNull;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.utils.json.JsonBean;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping(GRANT_CLASS_NAME)
public class GrantDataImpl implements GrantData {

    private final Long id, objectId;
    private final long roleId;
    private final String className, objectCode;
    private final GrantMode mode;
    private final String privilegeFilter;
    private final Map<String, String> attrPrivileges;
    private final Map<String, String> dmsPrivileges;
    private final Map<String, String> gisPrivileges;
    private final Map<String, Object> customPrivileges;
    private final PrivilegedObjectType type;

    private GrantDataImpl(GrantDataImplBuilder builder) {
        this.id = builder.id;
        this.roleId = builder.roleId;
        this.type = checkNotNull(builder.type);
        this.mode = checkNotNull(builder.mode);
        switch (type) {
            case POT_CLASS -> {
                checkArgument(EnumSet.of(GM_NONE, GM_READ, GM_WRITE).contains(mode), "invalid mode for class grant = %s", mode);
                this.className = checkNotBlank(toStringOrNull(firstNotNullOrNull(builder.className, builder.objectIdOrClassName)), "class name cannot be null");
                this.objectId = null;
                this.objectCode = null;
                this.privilegeFilter = builder.privilegeFilter;
                this.customPrivileges = isNullOrEmpty(builder.customPrivileges) ? emptyMap() : ImmutableMap.copyOf(builder.customPrivileges);
                this.attrPrivileges = isNullOrEmpty(builder.attrPrivileges) ? emptyMap() : ImmutableMap.copyOf(builder.attrPrivileges);
                this.dmsPrivileges = isNullOrEmpty(builder.dmsPrivileges) ? emptyMap() : ImmutableMap.copyOf(builder.dmsPrivileges);
                this.gisPrivileges = isNullOrEmpty(builder.gisPrivileges) ? emptyMap() : ImmutableMap.copyOf(builder.gisPrivileges);
            }
            case POT_PROCESS -> {
                checkArgument(EnumSet.of(GM_NONE, GM_WF_DEFAULT, GM_WF_PLUS, GM_WF_BASIC, GM_WF_ADMIN).contains(mode), "invalid mode for process grant = %s", mode);
                this.className = checkNotBlank(toStringOrNull(firstNotNullOrNull(builder.className, builder.objectIdOrClassName)), "class name cannot be null");
                this.objectId = null;
                this.objectCode = null;
                this.privilegeFilter = builder.privilegeFilter;
                this.customPrivileges = isNullOrEmpty(builder.customPrivileges) ? emptyMap() : ImmutableMap.copyOf(builder.customPrivileges);
                this.attrPrivileges = isNullOrEmpty(builder.attrPrivileges) ? emptyMap() : ImmutableMap.copyOf(builder.attrPrivileges);
                this.dmsPrivileges = isNullOrEmpty(builder.dmsPrivileges) ? emptyMap() : ImmutableMap.copyOf(builder.dmsPrivileges);
                this.gisPrivileges = null;
            }
            case POT_VIEW -> {
                checkArgument(EnumSet.of(GM_NONE, GM_READ, GM_WRITE).contains(mode), "invalid mode for view grant = %s", mode);
                this.className = null;
                this.objectId = checkNotNull(toLongOrNull(firstNotNullOrNull(builder.objectId, builder.objectIdOrClassName)), "object id cannot be null");
                this.objectCode = null;
                this.privilegeFilter = builder.privilegeFilter;
                this.customPrivileges = isNullOrEmpty(builder.customPrivileges) ? emptyMap() : ImmutableMap.copyOf(builder.customPrivileges);
                this.attrPrivileges = isNullOrEmpty(builder.attrPrivileges) ? emptyMap() : ImmutableMap.copyOf(builder.attrPrivileges);
                this.dmsPrivileges = null;
                this.gisPrivileges = null;
            }
            case POT_FILTER -> {
                checkArgument(EnumSet.of(GM_NONE, GM_READ, GM_WRITE).contains(mode), "invalid mode for filter grant = %s", mode);
                this.className = null;
                this.objectId = checkNotNull(toLongOrNull(firstNotNullOrNull(builder.objectId, builder.objectIdOrClassName)), "object id cannot be null");
                this.objectCode = null;
                this.privilegeFilter = null;
                this.customPrivileges = emptyMap();
                this.attrPrivileges = null;
                this.dmsPrivileges = null;
                this.gisPrivileges = null;
            }
            case POT_REPORT -> {
                checkArgument(EnumSet.of(GM_NONE, GM_READ).contains(mode), "invalid mode for report grant = %s", mode);
                this.className = null;
                this.objectId = checkNotNull(toLongOrNull(firstNotNullOrNull(builder.objectId, builder.objectIdOrClassName)), "object id (report id) cannot be null");
                this.objectCode = null;
                this.privilegeFilter = null;
                this.customPrivileges = emptyMap();
                this.attrPrivileges = null;
                this.dmsPrivileges = null;
                this.gisPrivileges = null;
            }
            case POT_CUSTOMPAGE -> {
                checkArgument(EnumSet.of(GM_NONE, GM_READ).contains(mode), "invalid mode for custom page grant = %s", mode);
                this.className = null;
                this.objectId = checkNotNull(toLongOrNull(firstNotNullOrNull(builder.objectId, builder.objectIdOrClassName)), "object id (custom page id) cannot be null");
                this.objectCode = null;
                this.privilegeFilter = null;
                this.customPrivileges = emptyMap();
                this.attrPrivileges = null;
                this.dmsPrivileges = null;
                this.gisPrivileges = null;
            }
            case POT_DASHBOARD -> {
                checkArgument(EnumSet.of(GM_NONE, GM_READ).contains(mode), "invalid mode for dashboard grant = %s", mode);
                this.className = null;
                this.objectId = checkNotNull(toLongOrNull(firstNotNullOrNull(builder.objectId, builder.objectIdOrClassName)), "object id (dashboard id) cannot be null");
                this.objectCode = null;
                this.privilegeFilter = null;
                this.customPrivileges = emptyMap();
                this.attrPrivileges = null;
                this.dmsPrivileges = null;
                this.gisPrivileges = null;
            }
            case POT_ETLTEMPLATE -> {
                checkArgument(EnumSet.of(GM_NONE, GM_READ).contains(mode), "invalid mode for import export template grant = %s", mode);
                this.className = null;
                this.objectId = null;
                this.objectCode = checkNotBlank(toStringOrNull(firstNotNullOrNull(builder.objectCode, builder.objectIdOrClassName)), "object code cannot be null");
                this.privilegeFilter = null;
                this.customPrivileges = emptyMap();
                this.attrPrivileges = null;
                this.dmsPrivileges = null;
                this.gisPrivileges = null;
            }
            case POT_ETLGATE -> {
                checkArgument(EnumSet.of(GM_NONE, GM_READ).contains(mode), "invalid mode for import export template grant = %s", mode);
                this.className = null;
                this.objectId = null;
                this.objectCode = checkNotBlank(toStringOrNull(firstNotNullOrNull(builder.objectCode, builder.objectIdOrClassName)), "object code cannot be null");
                this.privilegeFilter = null;
                this.customPrivileges = emptyMap();
                this.attrPrivileges = null;
                this.dmsPrivileges = null;
                this.gisPrivileges = null;
            }
            default ->
                throw unsupported("unsupported grant type = %s", type);
        }
    }

    @Override
    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr(GRANT_ATTR_ROLE_ID)
    public long getRoleId() {
        return roleId;
    }

    @Override
    @Nullable
    @CardAttr(GRANT_ATTR_PRIVILEGED_CLASS_ID)
    public String getClassName() {
        return className;
    }

    @Override
    @Nullable
    @CardAttr(GRANT_ATTR_PRIVILEGED_OBJECT_ID)
    public Long getObjectId() {
        return objectId;
    }

    @Override
    @Nullable
    @CardAttr(GRANT_ATTR_PRIVILEGED_OBJECT_CODE)
    public String getObjectCode() {
        return objectCode;
    }

    @Override
    public GrantMode getMode() {
        return mode;
    }

    @CardAttr(GRANT_ATTR_MODE)
    public String getModeAsString() {
        return serializeEnum(mode);
    }

    @Override
    @Nullable
    @CardAttr(GRANT_ATTR_PRIVILEGE_FILTER)
    public String getPrivilegeFilter() {
        return privilegeFilter;
    }

    @Override
    @CardAttr(GRANT_ATTR_UI_CARD_EDIT_MODE)
    @JsonBean
    public Map<String, Object> getCustomPrivileges() {
        return customPrivileges;
    }

    @Override
    @CardAttr(GRANT_ATTR_ATTRIBUTES_PRIVILEGES)
    @JsonBean
    public Map<String, String> getAttributePrivileges() {
        return attrPrivileges;
    }

    @Override
    @CardAttr(GRANT_ATTR_DMS_PRIVILEGES)
    @JsonBean
    public Map<String, String> getDmsPrivileges() {
        return dmsPrivileges;
    }

    @Override
    @CardAttr(GRANT_ATTR_GIS_PRIVILEGES)
    @JsonBean
    public Map<String, String> getGisPrivileges() {
        return gisPrivileges;
    }

    @Override
    public PrivilegedObjectType getType() {
        return type;
    }

    @CardAttr(GRANT_ATTR_TYPE)
    public String getTypeAsString() {
        return serializeEnum(type);
    }

    @Override
    public String toString() {
        return "GrantData{" + "id=" + id + ", subject=" + getObjectIdOrClassNameOrCode() + ", roleId=" + roleId + ", mode=" + mode + ", type=" + type + '}';
    }

    public static GrantDataImplBuilder builder() {
        return new GrantDataImplBuilder();
    }

    public static GrantDataImplBuilder copyOf(GrantData source) {
        return new GrantDataImplBuilder()
                .withId(source.getId())
                .withType(source.getType())
                .withRoleId(source.getRoleId())
                .withClassName(source.getClassName())
                .withObjectId(source.getObjectId())
                .withObjectCode(source.getObjectCode())
                .withMode(source.getMode())
                .withPrivilegeFilter(source.getPrivilegeFilter())
                .withCustomPrivileges(source.getCustomPrivileges())
                .withDmsPrivileges(source.getDmsPrivileges())
                .withGisPrivileges(source.getGisPrivileges())
                .withAttributePrivileges(source.getAttributePrivileges());
    }

    public static GrantDataImpl build(Role role, Classe classe, GrantMode mode) {
        return builder().withRoleId(role.getId()).withClass(classe).withMode(mode).build();
    }

    public static class GrantDataImplBuilder implements Builder<GrantDataImpl, GrantDataImplBuilder> {

        private Long id, roleId;
        private Long objectId;
        private String objectCode;
        private Object objectIdOrClassName;
        private GrantMode mode;
        private String className, privilegeFilter;
        private Map<String, Object> customPrivileges;
        private Map<String, String> dmsPrivileges;
        private Map<String, String> gisPrivileges;
        private Map<String, String> attrPrivileges;
        private PrivilegedObjectType type;

        public GrantDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public GrantDataImplBuilder withRoleId(Long roleId) {
            this.roleId = roleId;
            return this;
        }

        public GrantDataImplBuilder withClassName(String classOid) {
            this.className = classOid;
            return this;
        }

        public GrantDataImplBuilder withClass(Classe classe) {
            return this.withClassName(classe.getName()).withType(classe.isProcess() ? POT_PROCESS : POT_CLASS);
        }

        public GrantDataImplBuilder withObjectIdOrClassName(Object objectIdOrClassName) {
            this.objectIdOrClassName = objectIdOrClassName;
            return this;
        }

        public GrantDataImplBuilder withObjectId(Long objectId) {
            this.objectId = objectId;
            return this;
        }

        public GrantDataImplBuilder withObjectCode(String objectCode) {
            this.objectCode = toStringOrNull(objectCode);
            return this;
        }

        public GrantDataImplBuilder withMode(GrantMode mode) {
            this.mode = mode;
            return this;
        }

        public GrantDataImplBuilder withModeAsString(String mode) {
            this.mode = parseEnum(mode, GrantMode.class);
            return this;
        }

        public GrantDataImplBuilder withType(PrivilegedObjectType type) {
            this.type = type;
            return this;
        }

        public GrantDataImplBuilder withTypeAsString(String type) {
            this.type = parseEnum(type, PrivilegedObjectType.class);
            return this;
        }

        public GrantDataImplBuilder withPrivilegeFilter(String privilegeFilter) {
            this.privilegeFilter = privilegeFilter;
            return this;
        }

        public GrantDataImplBuilder withCustomPrivileges(Object... customPrivileges) {
            return this.withCustomPrivileges(map(customPrivileges));
        }

        public GrantDataImplBuilder withCustomPrivileges(Map<String, Object> customPrivileges) {
            this.customPrivileges = customPrivileges;
            return this;
        }

        public GrantDataImplBuilder withDmsPrivileges(Map<String, String> dmsPrivileges) {
            this.dmsPrivileges = dmsPrivileges;
            return this;
        }

        public GrantDataImplBuilder withGisPrivileges(Map<String, String> gisPrivileges) {
            this.gisPrivileges = gisPrivileges;
            return this;
        }

        public GrantDataImplBuilder withAttributePrivileges(Map<String, String> attrPrivileges) {
            this.attrPrivileges = attrPrivileges;
            return this;
        }

        @Override
        public GrantDataImpl build() {
            return new GrantDataImpl(this);
        }

    }
}
