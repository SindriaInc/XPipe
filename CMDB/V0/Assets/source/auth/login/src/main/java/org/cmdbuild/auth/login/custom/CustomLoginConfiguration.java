/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login.custom;

import jakarta.annotation.Nullable;

public interface CustomLoginConfiguration {

    boolean isCustomLoginEnabled();

    String getCustomLoginHandlerScript();

    @Nullable
    String getCustomLoginHandlerLanguage();

    @Nullable
    String getCustomLoginHandlerScriptClasspath();
}
