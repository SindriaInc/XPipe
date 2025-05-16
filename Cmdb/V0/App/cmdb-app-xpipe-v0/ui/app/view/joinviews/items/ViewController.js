Ext.define('CMDBuildUI.view.joinviews.items.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.joinviews-items-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            tabchange: 'onTabChange'
        }
    },

    /**
     * @param {CMDBuildUI.view.views.items.View} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this,
            vm = view.lookupViewModel(),
            height = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.inlinecard.height);

        view.setHeight(view.up().getHeight() * height / 100);
        CMDBuildUI.util.helper.FormHelper.renderFormForType(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.view,
            vm.get("objectTypeName"),
            {
                mode: CMDBuildUI.util.helper.FormHelper.formmodes.read,
                showAsFieldsets: true,
                linkName: 'record'
            }
        ).then(function (items) {
            view.removeAll();
            me.addTabs(items);
        });
    },

    /**
     * 
     * @param {Ext.panel.Panel} view 
     * @param {Ext.panel.Panel} prevtabpanel 
     * @param {Object} action 
     */
    onActivate: function (view, prevtabpanel, action) {
        var vm = view.lookupViewModel(),
            record = vm.get("record"),
            store = vm.get("involvedCards");

        store.removeAll();
        Ext.Object.each(record.getData(), function (key, value, object) {
            if (key.indexOf("CmJoinTableDummyRef_") === 0) {
                var classItem = key.split("_")[1];
                if (value) {
                    store.add({
                        id: value,
                        typeName: classItem,
                        code: record.get("_" + key + "_code"),
                        description: record.get("_" + key + "_description"),
                        type: CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(classItem)
                    });
                }
            }
        })
    },

    /**
     * 
     * @param {Object} items 
     */
    addTabs: function (items) {
        var view = this.getView(),
            activetab, tabCard, tabSources;

        items[0].fieldDefaults = CMDBuildUI.util.helper.FormHelper.fieldDefaults;

        tabCard = view.add({
            xtype: 'panel',
            iconCls: 'x-fa fa-file-text',
            itemId: 'dataView',
            items: items,
            autoScroll: true,
            tabConfig: {
                tabIndex: 0,
                title: CMDBuildUI.locales.Locales.common.tabs.card
            }
        });

        tabSources = view.add({
            xtype: 'panel',
            iconCls: 'x-fa fa-link',
            itemId: 'involvedcards',
            items: {
                xtype: 'joinviews-items-involvedcards-grid'
            },
            autoScroll: true,
            tabConfig: {
                tabIndex: 1,
                title: CMDBuildUI.locales.Locales.common.tabs.sources
            },
            listeners: {
                activate: 'onActivate'
            }
        });

        if (CMDBuildUI.util.Navigation.getCurrentRowTab() === "involvedCards") {
            activetab = tabSources;
        } else {
            activetab = tabCard;
        }

        Ext.asap(function () {
            if (!view.destroyed) {
                view.setActiveTab(activetab);
            }
        });
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.TabPanel} view
     * @param {Ext.Component} newtab
     * @param {Ext.Component} oldtab
     * @param {Object} eOpts
     */
    onTabChange: function (view, newtab, oldtab, eOpts) {
        if (newtab.getItemId() === "dataView") {
            CMDBuildUI.util.Navigation.updateCurrentRowTab("viewData");
        } else {
            CMDBuildUI.util.Navigation.updateCurrentRowTab("involvedCards");
        }
    }

});
