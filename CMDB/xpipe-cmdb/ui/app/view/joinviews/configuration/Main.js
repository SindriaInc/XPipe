Ext.define('CMDBuildUI.view.joinviews.configuration.Main', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.joinviews.configuration.MainController',
        'CMDBuildUI.view.joinviews.configuration.MainModel'
    ],
    alias: 'widget.joinviews-configuration-main',
    itemId: 'joinviews-configuration-main',
    controller: 'joinviews-configuration-main',
    viewModel: {
        type: 'joinviews-configuration-main'
    },
    userCls: 'formmode-view',
    bind: {
        userCls: '{formModeCls}'
    },
    listeners: {
        classchange: 'onClassChange',
        classaliaschange: 'onClassAliasChange',
        domainchange: 'onDomainChange',
        domaincheckchange: 'onDomainCheckChange',
        attributegruopchanged: 'onAttributeGroupsChanged',
        attributegruopremoved: 'onAttributeGroupsRemoved'
    },
    config: {
        theView: null
    },
    publishes: ['theView'],
    modelValidation: true,

    autoScroll: false,

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    layout: 'fit',

    items: [{
        xtype: 'joinviews-configuration-items-generalproperties',
        hidden: true,
        viewModel: {},
        bind: {
            hidden: '{actions.empty || currentStep !== 0}'
        }
    }, {
        xtype: 'joinviews-configuration-items-domains',
        hidden: true,
        viewModel: {},
        bind: {
            hidden: '{actions.empty || currentStep !== 1}'
        }
    }, {
        xtype: 'joinviews-configuration-items-fieldsets',
        hidden: true,
        viewModel: {},
        bind: {
            hidden: '{actions.empty || currentStep !== 2}'
        }
    }, {
        xtype: 'joinviews-configuration-items-attributeschoice',
        hidden: true,
        viewModel: {},
        bind: {
            hidden: '{actions.empty || currentStep !== 3}'
        }
    }, {
        xtype: 'joinviews-configuration-items-attributescustomization',
        hidden: true,
        viewModel: {},
        bind: {
            hidden: '{actions.empty || currentStep !== 4}'
        }
    }, {
        xtype: 'joinviews-configuration-items-filterscontainer',
        hidden: true,
        viewModel: {},
        bind: {
            hidden: '{actions.empty || currentStep !== 5}'
        }
    }, {
        xtype: 'joinviews-configuration-items-datasorting',
        hidden: true,
        viewModel: {},
        bind: {
            hidden: '{actions.empty || currentStep !== 6}'
        }
    }],

    dockedItems: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        dock: 'top',
        hidden: true,
        bind: {
            hidden: '{!actions.view}'
        },
        listeners: {},
        items: [{
            xtype: "button",
            itemId: "spacer",
            style: {
                visibility: "hidden"
            }
        }, {
            xtype: "tbfill"
        }, {
            xtype: "tool",
            itemId: "editBtn",
            glyph: "f040@FontAwesome",
            tooltip: CMDBuildUI.locales.Locales.joinviews.edit,
            localized: {
                tooltip: "CMDBuildUI.locales.Locales.joinviews.edit"
            },
            cls: "administration-tool",
            autoEl: {
                "data-testid": "conifgurablesviews-masterClassAlias-editBtn"
            },
            bind: {
                hidden: "{!actions.view}",
                disabled: "{theView._can_modify === false || toolAction._canUpdate === false}"
            }
        }, {
            xtype: "tool",
            itemId: "deleteBtn",
            glyph: "f014@FontAwesome",
            tooltip: CMDBuildUI.locales.Locales.joinviews.delete,
            localized: {
                tooltip: "CMDBuildUI.locales.Locales.joinviews.delete"
            },
            cls: "administration-tool",
            autoEl: {
                "data-testid": "conifgurablesviews-masterClassAlias-deleteBtn"
            },
            bind: {
                hidden: "{!actions.view}",
                disabled: "{theView._can_modify === false || toolAction._canDelete === false}"
            },
            listeners: {}
        }, {
            xtype: "container",
            cls: "x-tool-administration-tabandtools",
            bind: {
                hidden: "{!actions.view}"
            },
            items: [{
                xtype: "tool",
                itemId: "enableBtn",
                hidden: true,
                cls: "administration-tool",
                iconCls: "x-fa fa-check-circle-o",
                tooltip: CMDBuildUI.locales.Locales.joinviews.enable,
                localized: {
                    tooltip: "CMDBuildUI.locales.Locales.joinviews.enable"
                },
                autoEl: {
                    "data-testid": "conifgurablesviews-masterClassAlias-enableBtn"
                },
                bind: {
                    hidden: "{theView.active}",
                    disabled: "{theView._can_modify === false || toolAction._canActiveToggle === false}"
                }
            }, {
                xtype: "tool",
                itemId: "disableBtn",
                cls: "administration-tool",
                iconCls: "x-fa fa-ban",
                tooltip: CMDBuildUI.locales.Locales.administration.common.actions.disable,
                localized: {
                    tooltip: "CMDBuildUI.locales.Locales.administration.common.actions.disable"
                },
                hidden: true,
                autoEl: {
                    "data-testid": "conifgurablesviews-masterClassAlias-disableBtn"
                },
                bind: {
                    hidden: "{!theView.active}",
                    disabled: "{theView._can_modify === false || toolAction._canActiveToggle === false}"
                }
            }]
        }]
    }, {
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.empty}'
        },
        items: [{
            text: CMDBuildUI.locales.Locales.administration.common.actions.prev,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.prev'
            },
            itemId: 'prevBtn',
            bind: {
                disabled: '{isPrevDisabled}',
                ui: '{secondaryButtonUi}'
            },
            autoEl: {
                "data-testid": "conifgurablesviews-masterClassAlias-prevBtn"
            }
        }, {
            text: CMDBuildUI.locales.Locales.administration.common.actions.next,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.next'
            },
            itemId: 'nextBtn',
            bind: {
                disabled: '{isNextDisabled}',
                ui: '{secondaryButtonUi}'
            },
            autoEl: {
                "data-testid": "conifgurablesviews-masterClassAlias-nextBtn"
            }
        }, {
            xtype: 'component',
            flex: 1
        }, {
            text: CMDBuildUI.locales.Locales.administration.common.actions.save,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.save'
            },
            // formBind: true,
            itemId: 'saveBtn',
            bind: {
                hidden: '{actions.view}',
                disabled: '{currentStep !== totalStep -1}',
                ui: '{primaryButtonUi}'
            },
            autoEl: {
                "data-testid": "conifgurablesviews-masterClassAlias-saveBtn"
            }
        }, {
            text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.cancel'
            },
            itemId: 'cancelBtn',
            bind: {
                hidden: '{actions.view}',
                ui: '{secondaryButtonUi}'
            },
            autoEl: {
                "data-testid": "conifgurablesviews-masterClassAlias-cancelBtn"
            }
        }]
    }],

    /**
     * map for all aliases
     */
    aliases: null,

    /**
     * aliases types
     */
    aliasType: {
        klass: 'class',
        attribute: 'attribute'
    },

    /**
     * 
     * @param {String} alias
     * @param {String} type  (aliasType.klass|aliasType.attribute)
     * @param {Boolean} ingnoreAdd if true the alias will not be added to the aliases map
     */
    getNewAliasIndex: function (type, alias, ingnoreAdd) {
        var me = this;
        var indexFound = null;
        if (!me.aliases) {
            me.aliases = {};
        }
        if (!me.aliases[type]) {
            me.aliases[type] = {};
        }

        if (me.aliases[type][alias]) {
            var lastIndexChecked = null,
                found;
            Ext.Array.forEach(Ext.Object.getKeys(me.aliases[type][alias]), function (key) {
                lastIndexChecked = Number(key);
                if (me.aliases[type][alias][key] === false && !found) {
                    found = true;
                    indexFound = Number(key);
                }
            });
            if (Ext.isEmpty(indexFound) && !Ext.isEmpty(lastIndexChecked)) {
                indexFound = lastIndexChecked + 1;
            } else if (Ext.isEmpty(indexFound)) {
                indexFound = 0;
            }
            if (!ingnoreAdd) {
                if (!me.aliases[type][alias]) {
                    me.aliases[type][alias] = {};
                }

                me.aliases[type][alias][indexFound] = true;
            }
        } else {
            indexFound = 0;
            if (!ingnoreAdd) {
                if (!me.aliases[type][alias]) {
                    me.aliases[type][alias] = {};
                }
                me.aliases[type][alias][indexFound] = true;
            }
        }
        CMDBuildUI.util.Logger.log(Ext.String.format("New {0} alias for {1}: {2}", type, alias, indexFound), CMDBuildUI.util.Logger.levels.debug);
        return indexFound;
    },

    addAliasFromExisisting: function (type, alias) {
        var me = this,
            index = 0,
            base = alias,
            underscoreIndex = alias.lastIndexOf("_");
        if (underscoreIndex > 0) {
            index = alias.substring(underscoreIndex + 1);
            if (isNaN(index)) {
                index = 0;
            } else {
                base = alias.substring(0, underscoreIndex);
            }

        }

        if (index > -1) {
            if (!me.aliases) {
                me.aliases = {};
            }
            if (!me.aliases[type]) {
                me.aliases[type] = {};
            }
            if (!me.aliases[type][base]) {
                me.aliases[type][base] = {};
            }
            if (me.aliases[type][base][index]) {
                CMDBuildUI.util.Logger.log(Ext.String.format("Duplicate {0} alias for {1} with index {2}", type, alias, index), CMDBuildUI.util.Logger.levels.error);
            }
            me.aliases[type][base][index] = true;
        }
    },

    clearAliasIndex: function (type, alias) {
        var regex = /\d+/g;
        var match = regex.exec(alias);
        var aliasWithoutIndex;
        if (!match) {
            match = {
                0: 0
            };
            aliasWithoutIndex = alias;
        }
        if (Ext.String.endsWith(alias, Ext.String.format('_{0}', match[0]))) {
            aliasWithoutIndex = alias.replace(Ext.String.format('_{0}', match[0]), '');
        }
        if (this.aliases && this.aliases[type] && this.aliases[type][aliasWithoutIndex] && this.aliases[type][aliasWithoutIndex][match[0]]) {
            this.aliases[type][aliasWithoutIndex][match[0]] = false;
        }

    },

    getJoinData: function () {
        var data = [];
        var vm = this.lookupViewModel();
        var joinStore = vm.get('theView').joinWith();
        joinStore.each(function (record) {
            var recordData = record.getData();
            delete recordData.id;
            data.push(recordData);
        });
        return data;
    },

    getAttributesData: function () {
        var data = [],
            index = 0,
            selections = this.down('joinviews-configuration-items-attributescustomization grid').getStore().getRange();
        Ext.Array.forEach(selections, function (selection) {
            selection.set("index", index++);
            data.push(selection.getData());
        });
        return data;
    },

    getAttributesGroups: function () {
        var data = [];
        var fieldsets = this.down('joinviews-configuration-items-fieldsets #groupingsAttributesGrid').getStore().getRange();
        Ext.Array.forEach(fieldsets, function (selection) {
            data.push(selection.getData());
        });
        return data;
    },

    getSorter: function () {
        var data = [];
        var fieldsets = this.down('joinviews-configuration-items-datasorting #defaultOrderGrid').getStore().getRange();
        Ext.Array.forEach(fieldsets, function (selection) {
            var obj = selection.getData();
            data.push({
                property: obj.property,
                direction: obj.direction
            });
        });
        return data;
    },

    getFilterData: function () {
        var value = {
            attribute: this.down('joinviews-configuration-items-filterscontainer #attributesfilterpanel').getAttributesData()
        };
        value = (Ext.Object.isEmpty(value.attribute)) ? '' : Ext.encode(value);
        return value;
    }

});