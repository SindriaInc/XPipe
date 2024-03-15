Ext.define('CMDBuildUI.view.events.event.Mixin', {
    mixinId: 'view-events-event-mixin',

    config: {
        hideWidgets: false
    },

    /**
     * Add rules for fields visibility
     *
     * @deprecated
     */
    addConditionalVisibilityRules: Ext.emptyFn,

    /**
     * Add rules for fields visibility
     *
     * @deprecated
     */
    addAutoValueRules: Ext.emptyFn,

    /**
     *
     * @param {Object[]} items
     * @return {Object}
     */
    getMainPanelForm: function (items) {
        var me = this;

        // create panel
        var panelitems = [{
            flex: 1,
            scrollable: 'y',
            items: [{
                items: items
            }]
        }];

        if (!this.getHideWidgets()) {
            panelitems.push({
                xtype: 'widgets-launchers',
                formMode: this.formmode,
                bind: {
                    widgets: '{' + this.xtype + '.theEvent.widgets}'
                }
            });
        }
        return {
            flex: 1,
            layout: {
                type: 'hbox',
                align: 'stretch' //stretch vertically to parent
            },
            height: "100%",
            items: panelitems
        };
    },

    /**
     * Add rules for fields visibility
     */
    addAutoValueRules: function () {
        var vm = this.lookupViewModel();
        this.getForm().getFields().getRange().forEach(function (f) {
            if (f.setValueFromAutoValue !== undefined) {
                vm.bind(f.getAutoValueBind(), function (data) {
                    f.setValueFromAutoValue();
                });
            }
        });
    },

    getUserGroupParticipantsFields: function () {
        if ((this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.read || undefined)
            || (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.update && this.readonly == true)) {
            return this.getuserGroupParticipantsFieldsRead();
        } else {
            return this.getUserGroupParticipantsFieldsWrite();
        }
    },

    getuserGroupParticipantsFieldsRead: function () {

        var reference = this.getReference();

        return {
            xtype: 'container',
            layout: 'column',
            defaults: {
                columnWidth: 0.5,
                flex: 0.5,
                layout: "anchor",
                minHeight: 1,
                padding: "0 15 0 15",
                xtype: "fieldcontainer"
            },
            items: [{
                xtype: 'displayfield',
                hidden: false,
                fieldLabel: CMDBuildUI.locales.Locales.calendar.partecipantuser,
                localized: {
                    description: 'CMDBuildUI.locales.Locales.calendar.partecipantuser'
                },
                bind: {
                    value: Ext.String.format('{{0}.theEvent._participant_user_username}', reference)
                }
            }, {
                xtype: 'displayfield',
                hidden: false,
                fieldLabel: CMDBuildUI.locales.Locales.calendar.partecipantgroup,
                localized: {
                    description: 'CMDBuildUI.locales.Locales.calendar.partecipantgroup'
                },
                bind: {
                    value: Ext.String.format('{{0}.theEvent._participant_group_name}', reference)
                },
                reference: 'partecipantgroup',
                valueField: 'value',
                displayField: 'label',
                store: undefined
            }]
        }
    },

    getUserGroupParticipantsFieldsWrite: function () {
        var reference = this.getReference();
        return {
            xtype: 'container',
            layout: 'column',
            defaults: {
                columnWidth: 0.5,
                flex: 0.5,
                layout: "anchor",
                minHeight: 1,
                padding: "0 15 0 15",
                xtype: "fieldcontainer"
            },
            items: [{
                xtype: 'combobox',
                hidden: false,
                fieldLabel: CMDBuildUI.locales.Locales.calendar.partecipantuser,
                localized: {
                    description: 'CMDBuildUI.locales.Locales.calendar.partecipantuser'
                },
                bind: {
                    value: Ext.String.format('{{0}.theEvent._participant_user_id}', reference),
                    store: '{partecipantuserStore}'
                },
                reference: 'partecipantuser',
                valueField: 'value',
                displayField: 'label',
                viewModel: {
                    formulas: {
                        partecipantuserStore: {
                            bind: Ext.String.format('{{0}.theEvent._participant_users}', reference),
                            get: function (partecipantUser) {
                                // set the store of the partecipant user;
                                var theSession = CMDBuildUI.util.helper.SessionHelper.getCurrentSession(),
                                    // get the current user
                                    newData = [{
                                        label: theSession.get('userDescription'),
                                        value: theSession.get('userId')
                                    }];

                                // get the user set by the administration
                                if (partecipantUser && partecipantUser.length && partecipantUser[0]._id != theSession.get('userId')) {
                                    newData.push({
                                        label: partecipantUser[0].username,
                                        value: partecipantUser[0]._id
                                    })
                                }

                                // set the stroe for the comboox
                                return Ext.create('Ext.data.Store', {
                                    model: 'CMDBuildUI.model.base.ComboItem',
                                    proxy: {
                                        type: 'memory'
                                    },
                                    data: newData
                                });

                            }
                        }
                    }
                }
            }, {
                xtype: 'combobox',
                hidden: false,
                fieldLabel: CMDBuildUI.locales.Locales.calendar.partecipantgroup,
                localized: {
                    description: 'CMDBuildUI.locales.Locales.calendar.partecipantgroup'
                },
                bind: {
                    value: Ext.String.format('{{0}.theEvent._participant_group_id}', reference),
                    store: '{partecipantgroupStore}'
                },
                reference: 'partecipantgroup',
                valueField: 'value',
                displayField: 'label',
                viewModel: {
                    formulas: {
                        partecipantgroupStore: {
                            bind: Ext.String.format('{{0}.theEvent._participant_groups}', reference),
                            get: function (partecipantGroup) {
                                // set the store for groupuser;
                                var theSession = CMDBuildUI.util.helper.SessionHelper.getCurrentSession(),
                                    newData = [],
                                    // get all the available groups
                                    availableGroups = theSession.get('availableRolesExtendedData');

                                availableGroups.forEach(function (group) {
                                    newData.push({
                                        label: group._description_translation || group.description,
                                        value: group._id
                                    });
                                }, this);

                                // adds the group from the administration module
                                if (partecipantGroup && partecipantGroup.length) {
                                    var found = Ext.Array.findBy(newData, function (element, index) {
                                        if (element.value == partecipantGroup[0]._id) return true;
                                    }, this);

                                    if (!found) {
                                        newData.push({
                                            label: partecipantGroup[0].name,
                                            value: partecipantGroup[0]._id
                                        })
                                    }
                                }

                                // set the store in the combo
                                return Ext.create('Ext.data.Store', {
                                    model: 'CMDBuildUI.model.base.ComboItem',
                                    proxy: {
                                        type: 'memory'
                                    },
                                    data: newData
                                });
                            }
                        }
                    }
                }
            }]
        };
    },

    getNotificationTemplateComboField: function () {
        if (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.read || undefined
            || (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.update && this.readonly == true)) {
            return this.getNotificationTemplateComboFieldRead();
        } else {
            return this.getNotificationTemplateComboFieldWrite();
        }
    },
    getNotificationTemplateComboFieldRead: function () {//FIXME:make a better function
        var reference = this.getReference();
        var baseBind = Ext.String.format('{0}.theEvent', reference);
        var notificationTemplateBind = '{' + baseBind + '.notifications___0___template' + '}';

        var roField = CMDBuildUI.util.helper.FormHelper.getReadOnlyField(
            Ext.merge(
                this._getNotificationTemplateComboField(),
                {
                    cmdbuildtype: 'text',
                    attributeconf: {
                        editorType: null
                    },
                    disabled: true,
                    bind: {
                        hidden: '{hiddenField}'
                    },
                    viewModel: {
                        formulas: {
                            hiddenField: {
                                bind: {
                                    value: notificationTemplateBind
                                },
                                get: function (data) {
                                    return Ext.isEmpty(data.value);
                                }
                            }
                        }
                    }
                }
            ));
        return Ext.merge(
            this._getNotificationTemplateComboField(),
            roField
        );
    },
    getNotificationTemplateComboFieldWrite: function () {
        return this._getNotificationTemplateComboField();
    },
    _getNotificationTemplateComboField: function () {
        var reference = this.getReference();
        return {
            xtype: 'combobox',
            store: Ext.create('Ext.data.Store', {
                autoLoad: true,
                model: 'CMDBuildUI.model.emails.Template',
                proxy: {
                    type: 'baseproxy',
                    url: CMDBuildUI.util.api.Emails.getTemplatesUrl(),
                    extraParams: {
                        limit: 0
                    }
                },
                advancedFilter: {
                    attributes: {
                        provider: [{
                            operator: CMDBuildUI.util.helper.FiltersHelper.operators.equal,
                            value: ['email']
                        }]
                    }
                }
            }),
            fieldLabel: CMDBuildUI.locales.Locales.calendar.notificationtemplate,
            hidden: false,
            displayField: 'description',
            valueField: 'name',
            bind: {
                value: Ext.String.format('{{0}.theEvent.notifications___0___template}', reference)
            }
        };
    },

    /* NOTIFICATION CONTEN */
    getNotificationContentField: function () {
        if (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.read || undefined || (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.update && this.readonly == true)) {
            return this.getNotificationContentFieldRead();
        } else {
            return this.getNotificationContentFieldWrite();
        }
    },

    getNotificationContentFieldRead: function () {
        var field = this._getNotificationField();
        var roField = CMDBuildUI.util.helper.FormHelper.getReadOnlyField(field);
        return Ext.apply(field, roField);
    },

    getNotificationContentFieldWrite: function () {
        var field = this._getNotificationField();
        var editor = CMDBuildUI.util.helper.FormHelper.getEditorForField(field);
        return Ext.apply(field, editor);
    },

    _getNotificationField: function () {
        var reference = this.getReference();
        var baseBind = Ext.String.format('{0}.theEvent', reference);
        var notificationTemplateBind = '{' + baseBind + '.notifications___0___template' + '}';

        return {
            attributeconf: {
                editorType: null
                // showIf: "return !Ext.isEmpty(api.getValue('_notification_template'))"
            },
            fieldLabel: CMDBuildUI.locales.Locales.calendar.notificationtext,
            localized: {
                description: 'CMDBuildUI.locales.Locales.calendar.notificationtext'
            },
            hidden: true,
            bind: {
                value: Ext.String.format('{{0}.theEvent.notifications___0___content}', reference),
                hidden: '{hiddenField}'
            },
            cmdbuildtype: 'text',
            viewModel: {
                formulas: {
                    hiddenField: {
                        bind: {
                            value: notificationTemplateBind
                        },
                        get: function (data) {
                            return Ext.isEmpty(data.value);
                        }
                    }
                }
            }
        };
    },

    getDaysAdvanceNofificationField: function () {
        if ((this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.read || undefined) || (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.update && this.readonly == true)) {
            return this.getDaysAdvanceNofificationFieldRead();
        } else {
            return this.getDaysAdvanceNofificationFieldWrite();
        }
    },
    getDaysAdvanceNofificationFieldRead: function () {
        var field = this._getDaysAdvanceNofificationField();
        var roField = CMDBuildUI.util.helper.FormHelper.getReadOnlyField(field);
        var rField = Ext.applyIf(field, roField);
        return rField;
    },
    getDaysAdvanceNofificationFieldWrite: function () {
        var field = this._getDaysAdvanceNofificationField();
        var editor = CMDBuildUI.util.helper.FormHelper.getEditorForField(field);
        editor = Ext.applyIf(editor, {
            listeners: {
                change: {
                    scope: this,
                    fn: function (editor, value, startValue, eOpts) {
                        this.getTheEvent().set('notifications___0___delay', -value * (60 * 60 * 24));
                    }
                }
            }
        });
        return Ext.applyIf(field, editor);
    },
    _getDaysAdvanceNofificationField: function () {
        var reference = this.getReference();
        var baseBind = Ext.String.format('{0}.theEvent', reference);
        var notificationDelayBind = '{' + baseBind + '.notifications___0___delay' + '}';
        var notificationTemplateBind = '{' + baseBind + '.notifications___0___template' + '}';
        return {
            attributeconf: {
                editorType: null,
                showThousandsSeparator: false
                // unitOfMeasure: CMDBuildUI.locales.Locales.calendar.days
            },
            writable: true,
            fieldLabel: CMDBuildUI.locales.Locales.calendar.advancenotification,
            localized: {
                description: 'CMDBuildUI.locales.Locales.calendar.advancenotification'
            },
            hidden: true,
            bind: {
                value: '{daysAdvanceNotificationsField}'/* Ext.String.format('{{0}.theEvent._notification_delay}', reference) */,
                hidden: '{hiddenField}'
            },
            cmdbuildtype: 'string', //setting cmdbuildType = integer causes an error. That's because is missing unit of mesure
            viewModel: {
                data: {
                    daysAdvanceNotificationsField: null,
                    hiddenField: true
                },
                formulas: {
                    daysAdvanceNotificationsField: {
                        bind: {
                            value: notificationDelayBind
                        },
                        get: function (data) {
                            if (data.value) {
                                return Math.abs(-data.value / (60 * 60 * 24));
                            }
                        }
                    },
                    hiddenField: {
                        bind: {
                            value: notificationTemplateBind
                        },
                        get: function (data) {
                            return Ext.isEmpty(data.value);
                        }
                    }
                }
            }
        };
    },
    /* OPERATION FIELD */
    getOperationField: function () {
        var reference = this.getReference();
        var baseBind = Ext.String.format('{0}.theEvent', reference);
        var notificationStatusBind = '{' + baseBind + '.status' + '}';

        return {
            xtype: 'combobox',
            reference: 'operationcombo',
            fieldLabel: CMDBuildUI.locales.Locales.calendar.operation,
            displayField: 'label',
            valueField: 'value',
            bind: {
                value: Ext.String.format('{{0}.theEvent._operation}', reference),
                hidden: '{hiddenField}'
            },
            store: Ext.create('Ext.data.Store', {
                model: 'CMDBuildUI.model.base.ComboItem',
                proxy: {
                    type: 'memory'
                },
                data: [{
                    label: CMDBuildUI.locales.Locales.calendar.complete, value: 'completed'
                }, {
                    label: CMDBuildUI.locales.Locales.calendar.cancel, value: 'canceled'
                }]
            }),
            viewModel: {
                formulas: {
                    hiddenField: {
                        bind: {
                            value: notificationStatusBind
                        },
                        get: function (data) {
                            return data.value == "completed" || data.value == 'canceled';
                        }
                    }
                }
            }
        };
    },

    /* NOTIFICATION DELAY */
    getNotificationDelayField: function () {
        // var reference = this.getReference();

        if ((this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.read || undefined) || (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.update && this.readonly == true)) {
            return this.getNotificationDelayFieldRead();
        } else {
            return this.getNotificationDelayFieldWrite();
        }
    },

    // /** MISSING DAYS */
    getMissingDaysExtraConf: function () {
        var reference = this.getReference();
        return {
            bind: {
                value: {
                    bindTo: '{dateChange}'
                },
                hidden: {
                    bindTo: '{visibility}'
                }
            },
            renderer: function (value, field) {
                var color = (value < 0) ? 'red' : 'black';
                return '<span style="color:' + color + ';">' + value + '</span>';
            },
            viewModel: {
                formulas: {
                    dateChange: {
                        bind: Ext.String.format('{{0}.theEvent.date}', reference),
                        get: function (date) {
                            if (date) {
                                var now = new Date();
                                return (Ext.Date.diff(now, date, 'd') + 1) + '';
                            } else {
                                return '';
                            }
                        }
                    },
                    visibility: {
                        bind: {
                            status: Ext.String.format('{{0}.theEvent.status}', reference),
                            date: Ext.String.format('{{0}.theEvent.date}', reference)
                        },
                        get: function (data) {
                            if (data.status == 'expired' || data.status == 'active') {
                                return false;
                            } else {
                                return true;
                            }
                        }
                    }
                }
            }
        };

    },

    getClassCombobox: function () {
        var reference = this.getReference();
        var baseBind = Ext.String.format('{0}.theEvent', reference);
        var bind = '{' + baseBind + '._card_type' + '}';

        var selectableClasses = CMDBuildUI.util.helper.Configurations.get('cm_system_scheduler_selectableclasses');
        selectableClasses = selectableClasses.split(',');

        var store = Ext.create('Ext.data.ChainedStore', {
            source: Ext.getStore('classes.Classes'),
            grouper: {
                groupFn: function (item) {
                    if (item.isClass) {
                        return CMDBuildUI.locales.Locales.menu.classes;
                    }
                }
            },
            filters: [{
                property: '_id',
                operator: 'in',
                value: selectableClasses
            }]
        });

        return {
            xtype: 'groupedcombo',
            store: store,
            fieldLabel: CMDBuildUI.locales.Locales.calendar.class,
            displayField: 'description',
            valueField: '_id',
            value: this.getTheEvent().get('_card_type'),
            bind: {
                value: bind
            },
            queryMode: 'local',
            listeners: {
                change: {
                    fn: function (groupedcombo, newValue, oldValue, eOpts) {
                        var container = groupedcombo.up().up().items.items[1];
                        this.getTheEvent().set('card', null);
                        container.removeAll();

                        if (!Ext.isEmpty(newValue)) {
                            container.add(this.getCardCombobox(newValue));
                        }
                    },
                    scope: this
                }
            }
        };
    },

    getCardCombobox: function (targetType) {
        targetType = targetType || this.getTheEvent().get('_card_type');
        var value = this.getTheEvent().get('card');

        var reference = this.getReference();
        var baseBind = Ext.String.format('{0}.theEvent', reference);
        var bind = '{' + baseBind + '.card' + '}';

        return {
            xtype: 'referencecombofield',
            fieldLabel: CMDBuildUI.locales.Locales.calendar.associatedcard,
            displayField: 'Description',
            valueField: '_id',
            allowBlank: false,
            value: value,
            metadata: {
                targetType: CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(targetType),
                targetClass: targetType
            },
            name: 'card',
            bind: {
                value: bind
            }
        };
    },

    privates: {
        _missingdays_row_index: 0,
        _partecipantGroup_row_index: 3,
        _notification_delay_row_index: 4,
        _notificationText_row_index: 5,
        _operation_row_index: 6,
        _class_row_index: 8,
        _card_row_index: 8,

        getFormLayout: function () {
            return {
                _nogroup: {
                    rows: [{
                        columns: [{//row 0
                            fields: [{
                                attribute: 'date'
                            }]
                        }, {
                            fields: [{
                                attribute: 'missingDays'
                            }]
                        }]
                    }, {//row 1
                        columns: [{
                            fields: [{
                                attribute: 'category'
                            }]
                        }, {
                            fields: [{
                                attribute: 'priority'
                            }]
                        }]
                    }, {//row 2
                        columns: [{
                            fields: [{
                                attribute: 'description'
                            }]
                        }, {
                            fields: [{
                                attribute: 'content'
                            }]
                        }]
                    },
                    //Row 3 for partecipants;

                    {//Row 4
                        columns: (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.read) || (this.formmode == CMDBuildUI.util.helper.FormHelper.formmodes.update && this.readonly == true) ?
                            /**
                             * for read mode
                             */
                            [{
                                width: '0.5',
                                fields: [{
                                    attribute: ''//daysAdvanceNotification
                                }]
                            }]
                            :
                            /**
                             * for create/edit mode
                             */
                            [{
                                width: '0.5',
                                fields: [{
                                    attribute: '' //_notification_template
                                }]
                            }, {
                                width: '0.5',
                                fields: [{
                                    attribute: ''//daysAdvanceNotification
                                }]
                            }]
                    },

                    {//Row 5
                        columns: [{
                            fields: [{
                                attribute: ''//notifications___0___delay
                            }]
                        }]
                    },
                    {//Row 6
                        columns: [{
                            fields: [{
                                attribute: 'status'
                            }]

                        }, {
                            fields: [{
                                attribute: 'completion'
                            }]
                        }]
                    },
                    { //row 7
                        columns: [{
                            fields: [{
                                attribute: 'Type'
                            }]
                        }, {
                            fields: [{
                                attribute: 'status'
                            }]
                        }]
                    }, { //row 8
                        columns: [{
                            fields: [{
                                attribute: 'class'
                            }]
                        }, {
                            fields: [{
                                attribute: 'card'
                            }]
                        }]
                    }]
                }
            };
        },

        /**
         *
         * @param {[Object]} panel  the result of the function renderForm
         * @param {String} fieldName the field name to search
         */
        findFieldAfterRender: function (panel, fieldName) {
            return this._recursiveVisit(panel[0], fieldName);
        },

        /**
         *
         */
        _recursiveVisit: function (node, fieldName) {
            if (node.name == fieldName) {
                return node;
            }

            var v;
            if (node.items) {

                node.items.forEach(function (item, index, array) {
                    v = v || this._recursiveVisit(item, fieldName);
                }, this);
            }
            return v;
        }
    }
});