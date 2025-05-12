/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config.inner;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import javax.activation.DataSource;
import javax.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;
import org.cmdbuild.etl.config.WaterwayItem;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.toRecord;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.loadItems;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.prepareRecord;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;
import org.cmdbuild.etl.config.WaterwayDescriptorMeta;
import org.cmdbuild.etl.config.WaterwayDescriptorRepository;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.loadSystemDescriptors;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNullOrNull;
import org.springframework.context.annotation.Primary;

@Component
@Primary
public class WaterwayDescriptorRepositoryImpl implements WaterwayDescriptorRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;

    private final Map<String, WaterwayDescriptorRecord> systemDescriptors;

    public WaterwayDescriptorRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
        systemDescriptors = map(loadSystemDescriptors(), WaterwayDescriptorRecord::getCode).immutable();
        logger.info("system bus descriptors = {}", Joiner.on(", ").join(systemDescriptors.keySet()));
    }

    @Nullable
    @Override
    public WaterwayDescriptorRecord getDescriptorOrNull(String code) {
        return firstNotNullOrNull(dao.selectAll().from(WaterwayDescriptorRecord.class).where(ATTR_CODE, EQ, checkNotBlank(code)).getOneOrNull(), systemDescriptors.get(code));
    }

    @Override
    public WaterwayDescriptorRecord updateDescriptorMeta(String code, WaterwayDescriptorMeta meta) {
        WaterwayDescriptorRecord record = getDescriptor(code);
        return dao.update(toRecord(record.getData(), meta, record));
    }

    @Override
    public WaterwayDescriptorRecord createUpdateDescriptor(DataSource data, @Nullable WaterwayDescriptorMeta meta, boolean overwriteExisting) {
        WaterwayDescriptorRecord record = prepareRecord(data, meta, this::getDescriptorOrNull, this::getAllDescriptors);
        if (isNullOrLtEqZero(record.getId())) {
            record = dao.create(record);
        } else {
            checkArgument(overwriteExisting, "cannot overwrite descriptor with code =< %s >", record.getCode());
            record = dao.update(record);
        }
        return record;
    }

    @Override
    public List<WaterwayItem> getAllItems() {
        return loadItems(getAllDescriptors());
    }

    @Override
    public void deleteDescriptor(String code) {
        dao.delete(getDescriptor(code));
    }

    @Override
    public List<WaterwayDescriptorRecord> getAllDescriptors() {
        return list(map(systemDescriptors).with(map(dao.selectAll().from(WaterwayDescriptorRecord.class).orderBy(ATTR_CODE).asList(WaterwayDescriptorRecord.class), WaterwayDescriptorRecord::getCode)).values())
                .map(WaterwayDescriptorUtils::validateRecord).sorted(WaterwayDescriptorRecord::getCode);
    }

}
