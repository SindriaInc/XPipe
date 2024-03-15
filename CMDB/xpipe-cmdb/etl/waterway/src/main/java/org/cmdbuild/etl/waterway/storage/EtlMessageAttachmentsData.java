/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway.storage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentImpl;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentStorage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class EtlMessageAttachmentsData {

    private final String name, data;
    private final Map<String, String> meta;
    private final WaterwayMessageAttachmentStorage storage;
    private final WaterwayMessageAttachmentType type;

    public EtlMessageAttachmentsData(@JsonProperty("name") String name, @JsonProperty("data") String data, @JsonProperty("meta") Map<String, String> meta, @JsonProperty("storage") WaterwayMessageAttachmentStorage storage, @JsonProperty("type") WaterwayMessageAttachmentType type) {
        this.name = checkNotBlank(name);
        this.data = nullToEmpty(data);
        this.meta = map(meta).immutable();
        this.storage = checkNotNull(storage);
        this.type = checkNotNull(type);
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    @JsonIgnore
    public WaterwayMessageAttachmentStorage getStorage() {
        return storage;
    }

    @JsonIgnore
    public WaterwayMessageAttachmentType getType() {
        return type;
    }

    @JsonProperty("storage")
    public String getStorageAsString() {
        return serializeEnum(storage);
    }

    @JsonProperty("type")
    public String getTypeAsString() {
        return serializeEnum(type);
    }

    public static EtlMessageAttachmentsData fromAttachment(WaterwayMessageAttachment attachment) {
        String data;
        switch (attachment.getType()) {
            case WMAT_JSON, WMAT_TEXT ->
                data = attachment.getText();
            case WMAT_BYTES ->
                data = Base64.encodeBase64String(attachment.getBytes());
            default ->
                throw runtime("unsupported attachment type for serialization = %s", attachment.getType());
        }
        return new EtlMessageAttachmentsData(attachment.getName(), data, attachment.getMeta(), attachment.getStorage(), attachment.getType());
    }

    @JsonIgnore
    public WaterwayMessageAttachment toAttachment() {
        return WaterwayMessageAttachmentImpl.builder()
                .withName(name)
                .withStorage(storage)
                .withType(type)
                .withMeta(meta)
                .accept(b -> {
                    switch (type) {
                        case WMAT_JSON:
                        case WMAT_TEXT:
                            b.withObject(data);
                            break;
                        case WMAT_BYTES:
                            b.withObject(Base64.decodeBase64(data));
                            break;
                        default:
                            throw runtime("unsupported attachment type for deserialization = %s", type);
                    }
                }).build();
    }
}
