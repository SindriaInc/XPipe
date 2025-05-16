/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.temp;

import static com.google.common.base.Preconditions.checkNotNull;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.temp.TempDataImpl.TEMP_TABLE;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import org.cmdbuild.utils.json.JsonBean;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping(TEMP_TABLE)
public class TempDataImpl implements TempData {

    public static final String TEMP_TABLE = "_Temp";

    private final Long id;
    private final byte[] data;
    private final boolean isComposite;
    private final TempInfo info;
    private final String tempId;

    private TempDataImpl(TempDataImplBuilder builder) {
        this.id = builder.id;
        this.tempId = checkNotBlank(builder.tempId);
        this.data = checkNotNull(builder.data);
        this.isComposite = firstNotNull(builder.composite, false);
        this.info = firstNotNull(builder.info, TempInfoImpl.builder().build());
    }

    @Override
    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr
    public byte[] getData() {
        return data;
    }

    @Override
    @CardAttr
    public boolean isComposite() {
        return isComposite;
    }

    @Override
    @CardAttr
    @JsonBean(TempInfoImpl.class)
    public TempInfo getInfo() {
        return info;
    }

    @Override
    @CardAttr(ATTR_CODE)
    @Nullable
    public String getTempId() {
        return tempId;
    }

    @Override
    public String toString() {
        return "TempData{" + "id=" + id + ", tempId=" + tempId + '}';
    }

    public static TempDataImplBuilder builder() {
        return new TempDataImplBuilder();
    }

    public static TempDataImplBuilder copyOf(TempDataImpl source) {
        return new TempDataImplBuilder()
                .withId(source.getId())
                .withData(source.getData())
                .withComposite(source.isComposite())
                .withInfo(source.getInfo())
                .withTempId(source.getTempId());
    }

    public static class TempDataImplBuilder implements Builder<TempDataImpl, TempDataImplBuilder> {

        private Long id;
        private byte[] data;
        private Boolean composite;
        private TempInfo info;
        private String tempId;

        public TempDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public TempDataImplBuilder withData(byte[] data) {
            this.data = data;
            return this;
        }

        public TempDataImplBuilder withComposite(Boolean composite) {
            this.composite = composite;
            return this;
        }

        public TempDataImplBuilder withCompositionInfo(CompositionInfo info) {
            return this.withComposite(true).withData(toJson(info).getBytes(StandardCharsets.UTF_8));
        }

        public TempDataImplBuilder withInfo(TempInfo info) {
            this.info = info;
            return this;
        }

        public TempDataImplBuilder withTempId(@Nullable String tempId) {
            this.tempId = tempId;
            return this;
        }

        @Override
        public TempDataImpl build() {
            return new TempDataImpl(this);
        }

    }
}
