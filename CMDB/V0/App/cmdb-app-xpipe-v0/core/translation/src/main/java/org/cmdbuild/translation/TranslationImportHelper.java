/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation;

import javax.activation.DataHandler;
import javax.annotation.Nullable;

public interface TranslationImportHelper {

    TranslationImportHelper withSeparator(@Nullable String separator);

    void importTranslations(DataHandler data);

}
