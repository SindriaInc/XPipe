/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.function.BiConsumer;
import org.cmdbuild.email.EmailTemplate;
import org.cmdbuild.email.EmailTemplateBindings;
import org.cmdbuild.email.EmailTemplateService;
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
            EmailTemplateBindings bindings = templateService.getEmailTemplateBindings(template);
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
