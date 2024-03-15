package org.cmdbuild.uicomponents.data;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.util.Map;
import java.util.Objects;
import static java.util.function.Predicate.not;
import javax.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.ui.TargetDevice;
import static org.cmdbuild.ui.TargetDevice.TD_DEFAULT;
import static org.cmdbuild.ui.TargetDevice.TD_MOBILE;
import static org.cmdbuild.uicomponents.data.UiComponentData.UI_COMPONENT_TABLE_NAME;
import static org.cmdbuild.uicomponents.utils.UiComponentUtils.normalizeComponentData;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping(UI_COMPONENT_TABLE_NAME)
public class UiComponentDataImpl implements UiComponentData {

    private final Long id;
    private final String name, description;
    private final byte[] dataDefault, dataMobile;
    private final boolean isActive;
    private final UiComponentType type;

    private UiComponentDataImpl(UiComponentDataImplBuilder builder) {
        this.id = (builder.id);
        this.name = checkNotBlank(builder.name);
        this.description = nullToEmpty(builder.description);
        Map<TargetDevice, byte[]> data = normalizeComponentData((Map) map(TD_DEFAULT, builder.dataDefault, TD_MOBILE, builder.dataMobile).filterValues(not(Objects::isNull)::test));
        this.dataDefault = data.get(TD_DEFAULT);
        this.dataMobile = data.get(TD_MOBILE);
        this.isActive = firstNotNull(builder.isActive, true);
        this.type = checkNotNull(builder.type);
        checkArgument(dataDefault != null || dataMobile != null);
    }

    @Override
    @CardAttr
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr("Active")
    public boolean getActive() {
        return isActive;
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getName() {
        return name;
    }

    @Override
    @CardAttr
    public String getDescription() {
        return description;
    }

    @Override
    @CardAttr
    @Nullable
    public byte[] getDataDefault() {
        return dataDefault;
    }

    @Override
    @CardAttr
    @Nullable
    public byte[] getDataMobile() {
        return dataMobile;
    }

    @Override
    @CardAttr
    public UiComponentType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "UiComponentData{" + "id=" + id + ", name=" + name + '}';
    }

    public static UiComponentDataImplBuilder builder() {
        return new UiComponentDataImplBuilder();
    }

    public static UiComponentDataImplBuilder copyOf(UiComponentData source) {
        return new UiComponentDataImplBuilder()
                .withId(source.getId())
                .withName(source.getName())
                .withDescription(source.getDescription())
                .withActive(source.getActive())
                .withDataDefault(source.getDataDefault())
                .withDataMobile(source.getDataMobile())
                .withType(source.getType());
    }

    public static class UiComponentDataImplBuilder implements Builder<UiComponentDataImpl, UiComponentDataImplBuilder> {

        private Long id;
        private String name;
        private String description;
        private byte[] dataMobile, dataDefault;
        private Boolean isActive;
        private UiComponentType type;

        public UiComponentDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public UiComponentDataImplBuilder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public UiComponentDataImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public UiComponentDataImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public UiComponentDataImplBuilder withDataDefault(byte[] data) {
            this.dataDefault = data;
            return this;
        }

        public UiComponentDataImplBuilder withDataMobile(byte[] data) {
            this.dataMobile = data;
            return this;
        }

        public UiComponentDataImplBuilder withoutDataForTargetDevice(TargetDevice targetDevice) {
            switch (checkNotNull(targetDevice)) {
                case TD_DEFAULT ->
                    dataDefault = null;
                case TD_MOBILE ->
                    dataMobile = null;
            }
            return this;
        }

        public UiComponentDataImplBuilder withData(Map<TargetDevice, byte[]> data) {
            return this.withDataDefault(data.get(TD_DEFAULT)).withDataMobile(data.get(TD_MOBILE));
        }

        public UiComponentDataImplBuilder withType(UiComponentType type) {
            this.type = type;
            return this;
        }

        @Override
        public UiComponentDataImpl build() {
            return new UiComponentDataImpl(this);
        }

    }
}
