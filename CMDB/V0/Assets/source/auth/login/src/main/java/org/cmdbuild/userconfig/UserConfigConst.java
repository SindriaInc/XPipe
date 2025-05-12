/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.userconfig;

public interface UserConfigConst {

    static final String USER_CONFIG_MULTIGROUP = "cm_user_multiGroup",
            USER_CONFIG_LANGUAGE = "cm_user_language",
            USER_CONFIG_TIMEZONE = "cm_ui_timezone",
            USER_CONFIG_DATE_FORMAT_EXTJS = "cm_ui_dateFormat",
            USER_CONFIG_TIME_FORMAT_EXTJS = "cm_ui_timeFormat",
            USER_CONFIG_DATETIME_FORMAT_EXTJS = "cm_ui_dateTimeFormat",
            USER_CONFIG_DECIMAL_SEPARATOR = "cm_ui_decimalsSeparator",
            USER_CONFIG_THOUSANDS_SEPARATOR = "cm_ui_thousandsSeparator",
            USER_CONFIG_PREFERRED_OFFICE_SUITE = "cm_ui_preferredOfficeSuite",
            USER_CONFIG_PREFERRED_FILE_CHARSET = "cm_ui_preferredFileCharset",
            USER_CONFIG_PREFERRED_CSV_SEPARATOR = "cm_ui_preferredCsvSeparator",
            USER_CONFIG_MULTITENANT_ACTIVATION_PRIVILEGES = "cm_user_mutitenantActivationPrivileges";

//  
//    valid date/time formats (see ui/app/view/main/header/PreferencesModel.js): 
//    
//        dateFormatsData: function () {
//            return [{
//                label: 'dd/mm/yyyy',
//                value: 'd/m/Y'
//            }, {
//                label: 'dd-mm-yyyy',
//                value: 'd-m-Y'
//            }, {
//                label: 'dd.mm.yyyy',
//                value: 'd.m.Y'
//            }, {
//                label: 'mm/dd/yyyy',
//                value: 'm/d/Y'
//            }, {
//                label: 'yyyy/mm/dd',
//                value: 'Y/m/d'
//            }, {
//                label: 'yyyy-mm-dd',
//                value: 'Y-m-d'
//            }];
//        },
//
//        timeFormatsData: function () {
//            return [{
//                value: 'H:i:s',
//                label: CMDBuildUI.locales.Locales.main.preferences.twentyfourhourformat
//            }, {
//                value: 'h:i:s A',
//                label: CMDBuildUI.locales.Locales.main.preferences.twelvehourformat
//            }];
//        },
}
