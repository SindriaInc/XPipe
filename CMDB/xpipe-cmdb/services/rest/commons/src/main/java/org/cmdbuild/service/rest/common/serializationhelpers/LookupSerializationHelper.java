/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.function.BiConsumer;
import org.cmdbuild.common.beans.LookupValue;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.translation.ObjectTranslationService;
import org.springframework.stereotype.Component;

@Component
public class LookupSerializationHelper {

    private final ObjectTranslationService translationService;
    private final LookupService lookupService;

    public LookupSerializationHelper(ObjectTranslationService translationService, LookupService lookupService) {
        this.translationService = checkNotNull(translationService);
        this.lookupService = checkNotNull(lookupService);
    }

    public void serializeLookupValue(String name, LookupValue value, BiConsumer<String, Object> adder) {
        adder.accept(name, value.getCode());
        adder.accept(format("_%s_description", name), value.getDescription());
        adder.accept(format("_%s_description_translation", name), translationService.translateLookupDescriptionSafe(value.getLookupType(), value.getCode(), value.getDescription()));
    }

    public <E extends Enum> void serializeLookupValue(String name, E value, BiConsumer<String, Object> adder) {
        serializeLookupValue(name, lookupService.getLookup(value), adder);
    }
}
