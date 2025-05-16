Ext.define('CMDBuildUI.model.administration.MenuItem', {
    extend: 'Ext.data.TreeModel',

    requires: [
        'CMDBuildUI.proxy.MenuProxy'
    ],

    statics: {
        types: {
            folder: 'folder',
            klass: 'class',
            domain: 'domain',
            lookuptype: 'lookuptype',
            dmsmodel: 'dmsmodel',
            dmscategory: 'dmscategory',
            menu: 'menu',
            process: 'processclass',
            user: 'user',
            groupsandpermissions: 'groupsandpermissions',
            view: 'view',
            report: 'report',
            setup: 'setup',
            email: 'email',
            emailqueue: 'emailqueue',
            localization: 'localization',
            reportpdf: 'reportpdf',
            reportodt: 'reportodt',
            reportrtf: 'reportrtf',
            reportcsv: 'reportcsv',
            dashboard: 'dashboard',
            custompage: 'custompage',
            customcomponent: 'customcomponent',
            widget: 'widget',
            contextmenu: 'contextmenu',
            navigationtree: 'navigationtree',
            menunavigationtree: 'menunavigationtree',
            task: 'task',
            searchfilter: 'filter',
            schedule: 'schedule',
            script: 'script',
            gis: 'gis',
            bim: 'bim',
            importexport: 'etltemplate',
            gisgatetemplate: 'gisgatetemplate',
            databasegatetemplate: 'databasegatetemplate',
            ifcgatetemplate: 'ifcgatetemplate',
            home: 'home',
            bus: 'bus',
            servermanagement: 'servermanagement',
            notification: 'notification'
        }
    },

    fields: [{
        name: 'menutype',
        type: 'string',
        mapping: 'menuType'
    }, {
        name: 'selectable',
        type: 'boolean',
        persist: false,
        defaultValue: true
    }, {
        name: 'index',
        type: 'integer'
    }, {
        name: 'objecttype',
        type: 'string',
        mapping: 'objectType'
    }, {
        name: 'objectid',
        type: 'auto',
        mapping: 'objectId'
    }, {
        name: 'text',
        type: 'string',
        mapping: 'objectDescription'
    }, {
        name: 'leaf',
        type: 'boolean',
        persist: false
    }, {
        name: 'rowCls',
        type: 'string',
        persist: false
    }, {
        name: 'iconCls',
        type: 'string',
        persist: false,
        calculate: function (data) {
            switch (data.menutype) {
                case CMDBuildUI.model.menu.MenuItem.types.folder:
                    return 'x-fa fa-folder';
                case CMDBuildUI.model.menu.MenuItem.types.klass:
                    return 'x-fa fa-file-text-o';
                case CMDBuildUI.model.menu.MenuItem.types.process:
                    return 'x-fa fa-cog';
                case CMDBuildUI.model.menu.MenuItem.types.view:
                case CMDBuildUI.model.menu.MenuItem.types.lookuptype:
                    return 'x-fa fa-table';
                case CMDBuildUI.model.menu.MenuItem.types.report:
                    return 'x-fa fa-files-o';
                case CMDBuildUI.model.menu.MenuItem.types.reportpdf:
                    return 'x-fa fa-file-pdf-o';
                case CMDBuildUI.model.menu.MenuItem.types.reportodt:
                    return 'x-fa fa-file-o';
                case CMDBuildUI.model.menu.MenuItem.types.reportrtf:
                    return 'x-fa fa-file-o';
                case CMDBuildUI.model.menu.MenuItem.types.reportcsv:
                    return 'x-fa fa-file-o';
                case CMDBuildUI.model.menu.MenuItem.types.dashboard:
                    return 'x-fa fa-area-chart';
                case CMDBuildUI.model.menu.MenuItem.types.custompage:
                    return 'x-fa fa-code';
                case CMDBuildUI.model.administration.MenuItem.types.user:
                    return 'x-fa fa-user';
                case CMDBuildUI.model.administration.MenuItem.types.groupsandpermissions:
                    return 'x-fa fa-users';
                case CMDBuildUI.model.administration.MenuItem.types.email:
                    return 'x-fa fa-envelope';
                case CMDBuildUI.model.administration.MenuItem.types.notification:
                    return 'x-fa fa-bell';
                case CMDBuildUI.model.administration.MenuItem.types.emailqueue:
                    return 'x-fa fa-task';
                case CMDBuildUI.model.administration.MenuItem.types.localization:
                    return 'x-fa fa-globe';
                case CMDBuildUI.model.administration.MenuItem.types.task:
                    return 'x-fa fa-clock-o';
                case CMDBuildUI.model.administration.MenuItem.types.schedule:
                    return 'cmdbuild-customnavicon cmdbuildicon-stopwatch';
                case CMDBuildUI.model.administration.MenuItem.types.gis:
                    return 'x-fa fa-map-marker';
                case CMDBuildUI.model.administration.MenuItem.types.bim:
                    return 'x-fa fa-building';
            }
        }
    }],
    proxy: {
        type: 'menuproxy',
        url: '/sessions/current/'
    }
});