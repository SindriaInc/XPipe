Ext.define('CMDBuildUI.view.administration.content.tasks.Topbar', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.tasks.TopbarController'
    ],

    alias: 'widget.administration-content-tasks-topbar',
    controller: 'administration-content-tasks-topbar',
    viewModel: {},

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',

        items: [{
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.administration.tasks.texts.addtask,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.tasks.texts.addtask'
            },
            ui: 'administration-action-small',
            reference: 'addtask',
            itemId: 'addtask',
            iconCls: 'x-fa fa-plus',
            autoEl: {
                'data-testid': 'administration-user-toolbar-addTaskBtn'
            },
            menu: [],
            bind: {
                text: '{addTaskButtonText}',
                disabled: '{!toolAction._canAdd}'
            }
        }, {
            xtype: 'localsearchfield',
            gridItemId: '#taskGrid'
        }, '->', {
            xtype: 'container',
            bind: {
                html: '{servicesStatusLabel}'
            }
        }, {
            marginLeft: '5px',
            xtype: 'button',
            iconCls: 'fa fa-external-link',
            itemId: 'navigateToServices',
            cls: 'input-action-button-light',
            ui: 'administration-secondary-action-small'            
        }]
    }]
});