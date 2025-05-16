package org.cmdbuild.navtree;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import javax.annotation.Nullable;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.navtree.NavTreeType.NT_DEFAULT;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

@CardMapping("_NavTree")
public class NavTreeImpl implements NavTree {

    private final String name, description;
    private final Long id;
    private final NavTreeNode data;
    private final boolean active;
    private final NavTreeType type;

    private NavTreeImpl(NavTreeDataImplBuilder builder) {
        this.name = checkNotBlank(builder.name);
        this.description = nullToEmpty(builder.description);
        this.id = builder.id;
        this.data = checkNotNull(builder.data);
        this.active = firstNotNull(builder.active, true);
        this.type = firstNotNull(builder.type, NT_DEFAULT);
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getName() {
        return name;
    }

    @Nullable
    @CardAttr(ATTR_DESCRIPTION)
    @Override
    public String getDescription() {
        return description;
    }

    @Nullable
    @CardAttr(ATTR_ID)
    @Override
    public Long getId() {
        return id;
    }

    @CardAttr
    @Override
    public NavTreeNode getData() {
        return data;
    }

    @Override
    @CardAttr("Active")
    public boolean getActive() {
        return active;
    }

    @Override
    @CardAttr
    public NavTreeType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "NavTree{" + "name=" + name + ", id=" + id + '}';
    }

    public static NavTreeDataImplBuilder builder() {
        return new NavTreeDataImplBuilder();
    }

    public static NavTreeDataImplBuilder copyOf(NavTree source) {
        return new NavTreeDataImplBuilder()
                .withName(source.getName())
                .withDescription(source.getDescription())
                .withData(source.getData())
                .withId(source.getId())
                .withActive(source.getActive())
                .withType(source.getType());
    }

    public static class NavTreeDataImplBuilder implements Builder<NavTreeImpl, NavTreeDataImplBuilder> {

        private String name;
        private String description;
        private Long id;
        private NavTreeNode data;
        private Boolean active;
        private NavTreeType type;

        public NavTreeDataImplBuilder withType(NavTreeType type) {
            this.type = type;
            return this;
        }

        public NavTreeDataImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public NavTreeDataImplBuilder withData(NavTreeNode data) {
            this.data = data;
            return this;
        }

        public NavTreeDataImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public NavTreeDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public NavTreeDataImplBuilder withActive(Boolean active) {
            this.active = active;
            return this;
        }

        @Override
        public NavTreeImpl build() {
            return new NavTreeImpl(this);
        }

    }
}
