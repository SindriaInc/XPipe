/**
 * @file CMDBuildUI.util.Config
 * @module CMDBuildUI.util.Config
 * @author Tecnoteca srl
 * @access public
 */
(function () {
    window.cmdbuildConfig = window.cmdbuildConfig || {
        baseUrl: ""
    };
    Ext.define('CMDBuildUI.util.Config', {
        singleton: true,

        /**
         * @constant {String} baseUrl
         * Webservices base url.
         */
        baseUrl: window.cmdbuildConfig.baseUrl,

        /**
         * @constant {String} getesBaseUrl
         * Getes base url.
         */
        getesBaseUrl: window.cmdbuildConfig.baseUrl.replace("/rest/v3", "") + "/etl/gate",

        /**
         * @constant {String} socketUrl
         * Socket url.
         */
        socketUrl: window.cmdbuildConfig.socketUrl,

        /**
         * @constant {String} geoserverBaseUrl
         * GEO server url.
         */
        geoserverBaseUrl: window.cmdbuildConfig.geoserverBaseUrl,

        /**
         * @constant {String} bimserverBaseUrl
         * BIM server url.
         */
        bimserverBaseUrl: window.cmdbuildConfig.bimserverBaseUrl,

        /**
         * @constant {Numeric} ajaxTimeout
         * AJAX timeout in milliseconds.
         */
        ajaxTimeout: 15000, // milliseconds

        /**
         * @constant {String} uiBaseUrl
         * UI base url.
         */
        uiBaseUrl: window.location.origin + window.location.pathname,

        ui: {
            relations: {
                collapsedlimit: 10
            }
        },

        widgets: {
            customForm: 'widgets-customform-panel',
            linkCards: 'widgets-linkcards-panel',
            createModifyCard: 'widgets-createmodifycard-panel',
            goToCard: 'widgets-gotocard-panel',
            createReport: 'widgets-createreport-panel',
            openAttachment: 'widgets-attachmentwidget-panel',
            openNote: 'widgets-notewidget-panel',
            manageEmail: 'widgets-manageemail-panel',
            calendar: 'widgets-calendar-panel',
            workflow: 'widgets-startworkflow-panel',
            startWorkflow: 'widgets-startworkflow-panel',
            presetFromCard: 'widgets-presetfromcard-panel',
            sequenceView: 'widgets-sequenceview-panel'
        }
    });
})();