/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.fault;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.List;
import java.util.Map;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmExecutorUtils.safe;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.springframework.stereotype.Component;

@Component
public class FaultSerializationServiceImpl implements FaultSerializationService {

    private final ObjectTranslationService translationService;

    public FaultSerializationServiceImpl(ObjectTranslationService translationService) {
        this.translationService = checkNotNull(translationService);
    }

    @Override
    public List<Map<String, Object>> errorToJsonMessages(FaultEvent event) {
        return FaultUtils.errorToMessages(event).stream().map(this::buildMessageForResponse).collect(toImmutableList());
    }

    private Map<String, Object> buildMessageForResponse(FaultMessage e) {
        return (Map) map(
                "level", serializeEnum(e.getLevel()).toUpperCase(),
                "show_user", e.showUser(),
                "message", e.getMessage(),
                "_message_translation", e.hasCode() ? safe(() -> translationService.translateByCode(e.getCode(), e.getMessage()), e.getMessage()) : e.getMessage()).accept(m -> {
            if (e.hasCode()) {
                m.with("code", e.getCode());
            }
        });
    }
}
