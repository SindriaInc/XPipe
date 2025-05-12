/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import static org.cmdbuild.common.Constants.BASE_CLASS_NAME;
import static org.cmdbuild.common.Constants.DMS_MODEL_PARENT_CLASS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dms.DmsService;
import static org.cmdbuild.utils.io.CmStreamProgressUtils.buildProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("dmsMetadataSyncHelper")
public class DmsMetadataSyncHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DmsService service;
    private final DaoService dao;

    public DmsMetadataSyncHelper(DmsService service, DaoService dao) {
        this.service = checkNotNull(service);
        this.dao = checkNotNull(dao);
    }

    public void run() {
        checkArgument(service.isEnabled(), "dms service not enabled");
        logger.info("start migration/sync procedure; count records");
        long count = dao.selectCount().from(BASE_CLASS_NAME).getCount(),//TODO check simple class (??)
                documentCount = dao.selectCount().from(DMS_MODEL_PARENT_CLASS).getCount();
        logger.info("processing {} cards, current doc metadata count = {}", count, documentCount);
        AtomicLong i = new AtomicLong(0);
        Consumer<Long> listener = buildProgressListener(count, e -> logger.info("processing: {}", e.getProgressDescriptionDetailed()));
        dao.getAllClasses().stream().filter(c -> c.isStandardClass() && !c.isSuperclass()).forEach(c -> {//TODO check simple class (??)
            logger.debug("processing class = {}", c);
            dao.select(ATTR_ID).from(c).run().forEach(r -> {
                logger.trace("processing record = {}", r);
                service.getCardAttachments(c.getName(), r.get(ATTR_ID, Long.class), DaoQueryOptionsImpl.emptyOptions(), true);
                listener.accept(i.incrementAndGet());
            });
        });
        logger.info("migration/sync completed, analyzing");
        long documentCount2 = dao.selectCount().from(DMS_MODEL_PARENT_CLASS).getCount();
        logger.info("processed {} cards, doc metadata count = {} -> {}", i.get(), documentCount, documentCount2);
    }

}
