/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation;

import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.translation.dao.Translation;

public interface TranslationService extends ObjectTranslationService {

    @Nullable
    String translateExpr(@Nullable String source);

    @Nullable
    String getTranslationForCodeAndCurrentUser(String code);

    Map<String, String> getTranslationValueMapByLangForCode(String code);

    String getTranslationValueForCodeAndLang(String code, String lang);

    PagedElements<Translation> getTranslations(@Nullable String filter, @Nullable Integer offset, @Nullable Integer limit);

    Translation setTranslation(String code, String lang, String value);

    void deleteTranslationIfExists(String code, String lang);

    void deleteTranslations(String code);

    TranslationExportHelper exportHelper();

    TranslationImportHelper importHelper();
    
    void loadTranslationsForLanguages(Collection<String> languages);

}
