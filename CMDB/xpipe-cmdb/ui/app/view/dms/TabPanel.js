
Ext.define('CMDBuildUI.view.dms.TabPanel', {
    extend: 'CMDBuildUI.components.tab.FormPanel',
    alias: 'widget.dms-tabpanel',

    requires: [
        'CMDBuildUI.view.dms.TabPanelController',
        'CMDBuildUI.view.dms.TabPanelModel'
    ],

    mixins: [
        'CMDBuildUI.mixins.DetailsTabPanel'
    ],

    controller: 'dms-tabpanel',
    viewModel: {
        type: 'dms-tabpanel'
    },

    reference: 'dms-tabpanel',
    publishes: [
        'DMSCategoryValue',
        'DMSModelClassName',
        'DMSModelClass',
        'DMSClass',
        'attachmentId',
        'dmsModelAttachmentId'
    ],
    config: {

        DMSCategoryValue: {
            $value: null,
            evented: true
        },

        DMSCategoryDescription: {
            $value: null,
            evented: true
        },

        DMSModelClassName: {
            $value: null,
            evented: true
        },

        DMSModelClass: {
            $value: null,
            evented: true
        },

        DMSClass: {
            $value: undefined,
            evented: true
        },

        /**
         * @cfg {Ext.data.Model} theObject
         */
        theObject: null,

        attachmentId: {
            $value: undefined,
            evented: true
        },
        dmsModelAttachmentId: {
            $value: undefined,
            evented: true
        }
    },

    bind: {
        DMSCategoryValue: '{record.category}',
        DMSCategoryDescription: '{record._category_description_translation}',
        attachmentId: '{record._id}',
        dmsModelAttachmentId: '{record._card}',
        theObject: '{record}'
    },

    items: [{
        xtype: 'panel',
        layout: 'fit',
        title: CMDBuildUI.locales.Locales.common.tabs.attachment,
        localized: {
            title: 'CMDBuildUI.locales.Locales.common.tabs.attachment'
        },
        items: [{
            xtype: 'dms-attachment-view',
            bind: {
                objectType: '{dms-container.objectType}',
                objectTypeName: '{dms-container.objectTypeName}',
                objectId: '{dms-container.objectId}',
                attachmentId: '{dms-tabpanel.attachmentId}',
                DMSCategoryTypeName: '{dms-container.DMSCategoryTypeName}',
                DMSCategoryValue: '{dms-tabpanel.DMSCategoryValue}',
                theObject: '{record}'
            }
        }]
    }, {
        xtype: 'panel',
        layout: 'fit',
        items: [{
            xtype: 'dms-history-grid',
            itemId: 'dms-history-grid',
            bind: {
                objectType: '{dms-container.objectType}',
                objectTypeName: '{dms-container.objectTypeName}',
                objectId: '{dms-container.objectId}',
                attachmentId: '{dms-tabpanel.attachmentId}'
            }
        }],
        bind: {
            disabled: '{isRecordPhantom}'
        },
        title: CMDBuildUI.locales.Locales.common.tabs.history,
        localized: {
            title: 'CMDBuildUI.locales.Locales.common.tabs.history'
        }
    }, {
        xtype: 'events-grid',
        tabConfig: {
            title: CMDBuildUI.locales.Locales.common.tabs.schedules,
            localized: {
                title: 'CMDBuildUI.locales.Locales.common.tabs.schedules'
            },
            hidden: true,
            bind: {
                hidden: '{schedulesHidden}' //NOTE: the binding '{!dms-tabpanel.DMSClass._hasTriggers}' doesn't work, need to pass trougth dms-tabpanel viewModel
            }
        },
        height: '0',
        bind: {
            height: '{schedulesHeight}',
            eventsStore: '{eventsStore}'
        }
    }],

    ui: 'management',
    border: false,
    tabPosition: 'left',
    tabRotation: 0,
    header: false,

    defaults: {
        textAlign: 'left',
        bodyPadding: 10,
        scrollable: true,
        border: false
    },

    layout: 'fit',

    tabtools: Ext.Array.merge(
        CMDBuildUI.view.dms.Util.getTools(), [
        CMDBuildUI.view.dms.Util.getHelpTool({
            bind: {
                helpValue: '{dms-tabpanel.DMSClass.help}'
            }
        })
    ]),

    /**
     * 
     * @param {String} value 
     * @param {String} oldValue 
     * 
     * This function sets the attachmentid config only if the vm record (passed by the rowWidget) has a valid id. 
     * Useful when expanding the row and collapsing the parent grouping.
     */
    applyAttachmentId: function (value, oldValue) {
        return value;
        return Ext.isNumeric(value) ? value : null; //Was here for when collapsing the grouping and the passed record was a placeholder having defaul EXTJS id. it doesn't work for alfresco's id's
    }
});
