/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.virtual;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import org.cmdbuild.api.ApiConverterService;
import org.cmdbuild.corecomponents.CoreComponentService;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.entrytype.EntryTypeType.ET_CLASS;
import static org.cmdbuild.dao.entrytype.EntryTypeType.ET_DOMAIN;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FORMULA;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.utils.DatabaseRecordUtils;
import org.cmdbuild.script.ScriptService;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.script.ScriptType.ST_GROOVY;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VirtualAttributeServiceImpl implements VirtualAttributeService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CoreComponentService componentService;
    private final DaoService dao;
    private final ScriptService scriptService;
    private final ApiConverterService converterService;

    public VirtualAttributeServiceImpl(CoreComponentService componentService, DaoService dao, ScriptService scriptService, ApiConverterService converterService) {
        this.componentService = checkNotNull(componentService);
        this.dao = checkNotNull(dao);
        this.scriptService = checkNotNull(scriptService);
        this.converterService = checkNotNull(converterService);
    }

    @Override
    public <T extends DatabaseRecord> T loadVirtualAttributes(T databaseRecord) {
        return DatabaseRecordUtils.copyOf(databaseRecord).accept(c -> {
            databaseRecord.getType().getActiveServiceAttributes().stream().filter(a -> a.isOfType(FORMULA)).forEach(a -> {
                try {
                    Object value = null;
                    switch (a.getMetadata().getFormulaType()) {
                        case FT_SCRIPT -> {
                            logger.debug("load virtual attr = {} with script code =< {} > for card = {}", a, a.getMetadata().getFormulaCode(), databaseRecord);
                            String script = componentService.getComponent(a.getMetadata().getFormulaCode()).getData();
                            if (databaseRecord.getType().getEtType().equals(ET_CLASS)) {
                                try {
                                    value = scriptService.helper(getClass(), script, serializeEnum(ST_GROOVY)).withData(map("card", converterService.daoCardToApiCard((Card) databaseRecord))).executeForOutput();
                                } catch (Exception e) {
                                    logger.error("There was an error executing the script: {}", e.getCause().getMessage());
                                    value = "Script error";
                                }
                            } else if (databaseRecord.getType().getEtType().equals(ET_DOMAIN)) {
                                try {
                                    value = scriptService.helper(getClass(), script, serializeEnum(ST_GROOVY)).withData(map("card", converterService.daoRelationToApiRelation((CMRelation) databaseRecord))).executeForOutput();
                                } catch (Exception e) {
                                    logger.error("There was an error executing the script: {}", e.getCause().getMessage());
                                    value = "Script error";
                                }
                            }
                        }
                        case FT_SQL -> {
                            logger.debug("load virtual attr = {} with sql function =< {} > for card = {}", a, a.getMetadata().getFormulaCode(), databaseRecord);
                            StoredFunction function = dao.getFunctionByName(a.getMetadata().getFormulaCode());
                            value = getOnlyElement(dao.callFunction(function, map(getOnlyElement(function.getInputParameterNames(), "invalid function for virtual attr"), databaseRecord.getId())).values(), "invalid function for virtual attr");//TODO improve function mapping
                        }
                        default ->
                            throw unsupported("unsupported formula type = %s", a.getMetadata().getFormulaType());
                    }
                    c.withAttribute(a.getName(), value);
                } catch (Exception ex) {
                    throw new DaoException(ex, "error processing virtual attribute = %s for record = %s", a, databaseRecord);
                }
            });
        }).build();
    }

}
