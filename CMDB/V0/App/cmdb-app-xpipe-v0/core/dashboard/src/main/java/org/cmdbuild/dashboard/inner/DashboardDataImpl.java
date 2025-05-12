/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dashboard.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import javax.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.dashboard.DashboardData;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping("_Dashboard")
public class DashboardDataImpl implements DashboardData {

    private final String code, description, config;
    private final Long id;
    private final boolean isActive;

    private DashboardDataImpl(DashboardDataImplBuilder builder) {
        this.code = checkNotBlank(builder.code);
        this.description = nullToEmpty(builder.description);
        this.config = checkNotBlank(builder.config);
        this.id = builder.id;
        this.isActive = firstNotNull(builder.active, true);
    }

    @Override
    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getCode() {
        return code;
    }

    @Override
    @CardAttr(ATTR_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @CardAttr
    @Override
    public String getConfig() {
        return config;
    }

    @Override
    @CardAttr
    public boolean isActive() {
        return isActive;
    }

    public static DashboardDataImplBuilder builder() {
        return new DashboardDataImplBuilder();
    }

    public static DashboardDataImplBuilder copyOf(DashboardData source) {
        return new DashboardDataImplBuilder()
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withConfig(source.getConfig())
                .withId(source.getId())
                .withActive(source.isActive());
    }

    public static class DashboardDataImplBuilder implements Builder<DashboardDataImpl, DashboardDataImplBuilder> {

        private String code;
        private String description;
        private String config;
        private Long id;
        private Boolean active;

        public DashboardDataImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public DashboardDataImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public DashboardDataImplBuilder withConfig(String data) {
            this.config = data;
            return this;
        }

        public DashboardDataImplBuilder withData(Object data) {
            this.config = toJson(checkNotNull(data));
            return this;
        }

        public DashboardDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public DashboardDataImplBuilder withActive(Boolean active) {
            this.active = active;
            return this;
        }

        @Override
        public DashboardDataImpl build() {
            return new DashboardDataImpl(this);
        }

    }
}
