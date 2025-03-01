/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.function.BiConsumer;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.email.template.EmailTemplateService;
import org.cmdbuild.template.TemplateBindings;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplateSerializationHelper {

    private final EmailTemplateService templateService;
    private final ObjectTranslationService translationService;

    public EmailTemplateSerializationHelper(EmailTemplateService templateService, ObjectTranslationService translationService) {
        this.templateService = checkNotNull(templateService);
        this.translationService = checkNotNull(translationService);
    }

    public void serializeEmailTemplateBindings(EmailTemplate template, boolean includeBindings, BiConsumer<String, Object> adder) {
        if (includeBindings) {
            TemplateBindings bindings = templateService.fetchTemplateBindings(template);
            adder.accept("_bindings", map(
                    "client", bindings.getClientBindings(),
                    "server", bindings.getServerBindings()
            ));
        }
    }

    public void serializeEmailTemplateTranslation(EmailTemplate template, BiConsumer<String, Object> adder) {
        adder.accept("_description_translation", translationService.translateEmailTemplateDescription(template.getCode(), template.getDescription()));
    }
}
