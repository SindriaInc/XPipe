/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.uicomponents;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.cmdbuild.uicomponents.data.UiComponentType;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public class UiComponentInfoImpl implements UiComponentInfo {

    private final long id;
    private final String name, description, extjsComponentId, extjsAlias;
    private final boolean isActive;
    private final UiComponentType type;
    private final List<UiComponentVersionInfo> versions;

    private UiComponentInfoImpl(UiComponentInfoImplBuilder builder) {
        this.id = builder.id;
        this.isActive = builder.isActive;
        this.name = checkNotBlank(builder.name);
        this.description = firstNotBlank(builder.description, name);
        this.extjsComponentId = checkNotBlank(builder.extjsComponentId);
        this.extjsAlias = checkNotBlank(builder.extjsAlias);
        this.type = checkNotNull(builder.type);
        this.versions = checkNotEmpty(ImmutableList.copyOf(builder.versions));
        checkArgument(getTargetDevices().size() == versions.size());
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getExtjsComponentId() {
        return extjsComponentId;
    }

    @Override
    public String getExtjsAlias() {
        return extjsAlias;
    }

    @Override
    public UiComponentType getType() {
        return type;
    }

    @Override
    public List<UiComponentVersionInfo> getVersions() {
        return versions;
    }

    @Override
    public String toString() {
        return "ExtComponentInfo{" + "id=" + id + ", name=" + name + '}';
    }

    public static UiComponentInfoImplBuilder builder() {
        return new UiComponentInfoImplBuilder();
    }

    public static UiComponentInfoImplBuilder copyOf(UiComponentInfo source) {
        return new UiComponentInfoImplBuilder()
                .withId(source.getId())
                .withActive(source.isActive())
                .withName(source.getName())
                .withDescription(source.getDescription())
                .withExtjsComponentId(source.getExtjsComponentId())
                .withExtjsAlias(source.getExtjsAlias())
                .withType(source.getType())
                .withVersions(source.getVersions());
    }

    public static class UiComponentInfoImplBuilder implements Builder<UiComponentInfoImpl, UiComponentInfoImplBuilder> {

        private long id;
        private Boolean isActive;
        private String name;
        private String description;
        private String extjsComponentId;
        private String extjsAlias;
        private UiComponentType type;
        private List<UiComponentVersionInfo> versions;

        public UiComponentInfoImplBuilder withId(long id) {
            this.id = id;
            return this;
        }

        public UiComponentInfoImplBuilder withVersions(List<UiComponentVersionInfo> versions) {
            this.versions = versions;
            return this;
        }

        public UiComponentInfoImplBuilder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public UiComponentInfoImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public UiComponentInfoImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public UiComponentInfoImplBuilder withExtjsComponentId(String extjsComponentId) {
            this.extjsComponentId = extjsComponentId;
            return this;
        }

        public UiComponentInfoImplBuilder withExtjsAlias(String extjsAlias) {
            this.extjsAlias = extjsAlias;
            return this;
        }

        public UiComponentInfoImplBuilder withType(UiComponentType type) {
            this.type = type;
            return this;
        }

        @Override
        public UiComponentInfoImpl build() {
            return new UiComponentInfoImpl(this);
        }

    }
}
