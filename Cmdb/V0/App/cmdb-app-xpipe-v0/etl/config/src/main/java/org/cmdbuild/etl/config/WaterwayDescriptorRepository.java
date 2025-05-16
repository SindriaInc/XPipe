/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import javax.activation.DataSource;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecord;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;

public interface WaterwayDescriptorRepository {

    WaterwayDescriptorRecord createUpdateDescriptor(DataSource config, @Nullable WaterwayDescriptorMeta meta, boolean overwriteExisting);

    WaterwayDescriptorRecord updateDescriptorMeta(String code, WaterwayDescriptorMeta meta);

    @Nullable
    WaterwayDescriptorRecord getDescriptorOrNull(String code);

    void deleteDescriptor(String code);

    List<WaterwayDescriptorRecord> getAllDescriptors();

    List<WaterwayItem> getAllItems();

    default WaterwayDescriptorRecord createUpdateDescriptor(String config, @Nullable WaterwayDescriptorMeta meta) {
        return createUpdateDescriptor(newDataSource(config), meta, true);
    }

    default WaterwayDescriptorRecord createUpdateDescriptor(DataSource config, @Nullable WaterwayDescriptorMeta meta) {
        return createUpdateDescriptor(config, meta, true);
    }

    default WaterwayDescriptorRecord getDescriptor(String code) {
        return checkNotNull(getDescriptorOrNull(code), "descriptor not found for code =< %s >", code);
    }

    default WaterwayDescriptorRecord createUpdateDescriptor(DataSource config) {
        return createUpdateDescriptor(config, null);
    }

    default WaterwayItem getItemByCode(String code) {
        checkNotBlank(code);
        return getAllItems().stream().filter(i -> equal(i.getCode(), code)).collect(onlyElement("item not found for code =< %s >", code));
    }

}
