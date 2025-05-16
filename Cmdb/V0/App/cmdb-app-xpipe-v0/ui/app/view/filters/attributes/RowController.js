Ext.define('CMDBuildUI.view.filters.attributes.RowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.filters-attributes-row',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#operatorcombo': {
            change: 'onOperatorComboChange'
        },
        '#typecheck': {
            change: 'onTypeCkeckChange'
        },
        '#currentUser': {
            change: 'onCurrentUserCheckChange'
        },
        '#currentGroup': {
            change: 'onCurrentGroupCheckChange'
        },
        '#removebutton': {
            beforerender: 'onRemoveButtonBeforeRender'
            // click event is managed on parent panel
        }
    },

    onBeforeRender: function (view, eOpts) {
        var vm = view.lookupViewModel();
        // add values fields
        this.addValuesFields(vm.get("values.attribute"));
        if (vm.get("attachments.operator_label")) {
            view.down("#comboboxoperator").setMargin('0 0 0 15');
        };
    },

    /**
     * Executed when changes the value of Operator combobox.
     * 
     * @param {Ext.form.field.ComboBox} combo 
     * @param {String} newValue 
     * @param {String} oldValue 
     * @param {Object} eOpts 
     */
    onOperatorComboChange: function (combo, newValue, oldValue, eOpts) {
        this.updateTypeFieldVisibility();
        this.updateCurrentUserVisibility();
        this.updateCurrentGroupVisibility();
        this.updateValue1FieldVisibility();
        this.updateValue2FieldVisibility();
    },

    /**
     * Executed when changes the value of Operator combobox.
     * 
     * @param {Ext.form.field.Checkbox} check 
     * @param {Boolean} newValue 
     * @param {Boolean} oldValue 
     * @param {Object} eOpts 
     */
    onTypeCkeckChange: function (check, newValue, oldValue, eOpts) {
        this.updateCurrentUserVisibility();
        this.updateCurrentGroupVisibility();
        this.updateValue1FieldVisibility();
        this.updateValue2FieldVisibility();
    },

    /**
     * Executed when changes the value current user checkbox.
     * 
     * @param {Ext.form.field.Checkbox} check 
     * @param {Boolean} newValue 
     * @param {Boolean} oldValue 
     * @param {Object} eOpts 
     */
    onCurrentUserCheckChange: function (check, newValue, oldValue, eOpts) {
        var vm = this.getViewModel();
        vm.set("values.value1", null);
        this.updateValue1FieldVisibility();
        this.updateValue2FieldVisibility();
    },

    /**
     * Executed when changes the value current group checkbox.
     * 
     * @param {Ext.form.field.Checkbox} check 
     * @param {Boolean} newValue 
     * @param {Boolean} oldValue 
     * @param {Object} eOpts 
     */
    onCurrentGroupCheckChange: function (check, newValue, oldValue, eOpts) {
        var vm = this.getViewModel();
        vm.set("values.value1", null);
        this.updateValue1FieldVisibility();
        this.updateValue2FieldVisibility();
    },

    /**
     * @param {Ext.button.Button} button 
     * @param {Object} eOpts 
     */
    onRemoveButtonBeforeRender: function (button, eOpts) {
        this.applyUiIfAdmin(button);
    },

    privates: {
        /**
         * Add a container with value fields.
         * @param {String} attributename 
         */
        addValuesFields: function (attributename) {
            var container = this.lookupReference('valuescontainer');
            var vm = this.getViewModel();
            // empty container
            container.removeAll(true);
            var allattributes = vm.get("allfields");
            if (attributename && allattributes[attributename]) {
                var attribute = allattributes[attributename];
                attribute = Ext.merge({}, attribute);
                vm.set("attributetype", attribute.cmdbuildtype);

                if ((attribute.type === 'date' || attribute.cmdbuildtype === 'date') && vm.get('values.value1')) {
                    vm.set('values.value1', new Date(vm.get('values.value1')));
                    if (vm.get('values.value2')) {
                        vm.set('values.value2', new Date(vm.get('values.value2')));
                    }
                }
                var editor;
                switch (attribute.cmdbuildtype) {
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.activity:
                        editor = CMDBuildUI.util.helper.FormHelper.getActivityField(CMDBuildUI.util.helper.FormHelper.formmodes.update);
                        if (editor) {
                            editor.fieldLabel = null;
                            editor.hidden = true;
                            editor.bind.hidden = '{hiddenfields.value1}';
                            editor.bind.value = '{values.value1}';
                            editor.bind.disabled = '{displayOnly}';
                            container.add(
                                editor
                            );
                        }
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference:
                        if (this.getView().getAllowCurrentUser() && 'User' === attribute.attributeconf.targetClass) {

                            editor = {
                                xtype: 'bufferedcombo',
                                labelAlign: 'top',
                                valueField: '_id',
                                displayField: 'username',
                                columns: [{
                                    dataIndex: 'username',
                                    flex: 1
                                }],
                                name: 'userId',
                                storealias: 'users',
                                modelname: 'CMDBuildUI.model.users.User',
                                recordLinkName: 'values',
                                bind: {
                                    hidden: '{hiddenfields.value1}',
                                    value: '{values.value1}',
                                    disabled: '{displayOnly}'
                                }
                            };
                        }
                        // show current user or current group
                        else if (this.getView().getAllowCurrentGroup() && 'Group' === attribute.attributeconf.targetClass) {
                            editor = {
                                xtype: 'bufferedcombo',
                                labelAlign: 'top',
                                valueField: '_id',
                                displayField: 'description',
                                columns: [{
                                    dataIndex: 'description',
                                    flex: 1
                                }],
                                name: 'groupId',
                                storealias: 'groups',
                                modelname: 'CMDBuildUI.model.users.Group',
                                recordLinkName: 'values',
                                bind: {
                                    hidden: '{hiddenfields.value1}',
                                    value: '{values.value1}',
                                    disabled: '{displayOnly}'
                                }
                            };
                        }

                        if (editor) {
                            container.add(
                                editor
                            );
                        }

                        container.add({
                            xtype: 'textfield',
                            hidden: true,
                            bind: {
                                value: '{values.referencetext}',
                                hidden: '{hiddenfields.referencetext}',
                                disabled: '{displayOnly}'
                            }
                        });
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookupArray:
                        attribute.cmdbuildtype = "lookup"

                        container.add({
                            xtype: 'textfield',
                            hidden: true,
                            bind: {
                                value: '{values.referencetext}',
                                hidden: '{hiddenfields.referencetext}',
                                disabled: '{displayOnly}'
                            }
                        });
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup:
                        container.add({
                            xtype: 'textfield',
                            hidden: true,
                            bind: {
                                value: '{values.referencetext}',
                                hidden: '{hiddenfields.referencetext}',
                                disabled: '{displayOnly}'
                            }
                        });
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.tenant:
                        editor = CMDBuildUI.util.helper.FormHelper.getTenantField(CMDBuildUI.util.helper.FormHelper.formmodes.update);
                        container.add(
                            Ext.applyIf({
                                hidden: true,
                                fieldLabel: null,
                                bind: {
                                    value: '{values.value1}',
                                    hidden: '{hiddenfields.value1}',
                                    disabled: '{displayOnly}'
                                }
                            }, editor)
                        );
                        break;
                    case 'dmscategory':
                        editor = {
                            xtype: 'groupedcombobox',
                            valueField: 'value',
                            displayField: 'label',
                            queryMode: 'local',
                            forceSelection: true,
                            hidden: true,
                            bind: {
                                store: '{dmscategories}',
                                value: '{values.value1}',
                                hidden: '{hiddenfields.value1}',
                                disabled: '{displayOnly}'
                            }
                        }
                        container.add(editor);
                        break;
                }

                if (!editor) {
                    editor = CMDBuildUI.util.helper.FormHelper.getEditorForField(
                        attribute,
                        {
                            ignoreUpdateVisibilityToField: true,
                            ignoreCustomValidator: true,
                            ignoreAutovalue: true
                        }
                    );

                    if (editor.xtype === "threestatecheckboxfield") {
                        editor.xtype = "checkboxfield";
                    } else if (editor.xtype === "linkfield") {
                        editor.xtype = "textfield";
                    }

                    editor.ignoreCqlFilter = true;
                    container.add([
                        Ext.apply({
                            hidden: true,
                            bind: {
                                value: '{values.value1}',
                                hidden: '{hiddenfields.value1}',
                                disabled: '{displayOnly}'
                            }
                        }, editor),
                        Ext.apply({
                            hidden: true,
                            bind: {
                                value: '{values.value2}',
                                hidden: '{hiddenfields.value2}',
                                disabled: '{displayOnly}'
                            }
                        }, editor)
                    ]);
                }
            }
        },

        /**
         * @param {Ext.button.Button} button 
         */
        applyUiIfAdmin: function (button) {
            var vm = button.lookupViewModel();
            if (vm && vm.get('isAdministrationModule')) {
                button.ui = 'administration-action';
            }
        },

        updateTypeFieldVisibility: function () {
            var vm = this.getViewModel();
            var isHidden = true;
            var allowInputParameter = this.getView().getAllowInputParameter();
            var operator = vm.get("values.operator");
            if (vm.get("values.typeinput") && (operator === CMDBuildUI.util.helper.FiltersHelper.operators.null || operator === CMDBuildUI.util.helper.FiltersHelper.operators.notnull)) {
                vm.set("values.typeinput", false);
            }
            if (
                allowInputParameter &&
                operator &&
                operator !== CMDBuildUI.util.helper.FiltersHelper.operators.null &&
                operator !== CMDBuildUI.util.helper.FiltersHelper.operators.notnull
            ) {
                isHidden = false;
            }
            vm.set("hiddenfields.typeinput", isHidden);
        },

        updateCurrentUserVisibility: function () {
            var vm = this.getViewModel();
            var isHidden = true;
            var allowCurrentUser = this.getView().getAllowCurrentUser();
            var operator = vm.get("values.operator");
            var attribute = vm.get("values.attribute");
            var typeinput = vm.get("values.typeinput");
            if (
                attribute &&
                allowCurrentUser &&
                (operator === CMDBuildUI.util.helper.FiltersHelper.operators.equal ||
                    operator === CMDBuildUI.util.helper.FiltersHelper.operators.notequal) &&
                'User' === vm.get('allfields')[attribute].attributeconf.targetClass &&
                !typeinput
            ) {
                isHidden = false;
            }
            if (isHidden) {
                vm.set('values.currentUser', false);
            }
            vm.set("hiddenfields.currentUser", isHidden);
        },

        updateCurrentGroupVisibility: function () {
            var vm = this.getViewModel();
            var isHidden = true;
            var allowCurrentGroup = this.getView().getAllowCurrentGroup();
            var operator = vm.get("values.operator");
            var attribute = vm.get("values.attribute");
            var typeinput = vm.get("values.typeinput");
            if (
                attribute &&
                allowCurrentGroup &&
                (operator === CMDBuildUI.util.helper.FiltersHelper.operators.equal ||
                    operator === CMDBuildUI.util.helper.FiltersHelper.operators.notequal) &&
                'Role' === vm.get('allfields')[attribute].attributeconf.targetClass &&
                !typeinput
            ) {
                isHidden = false;
            }
            if (isHidden) {
                vm.set('values.currentGroup', false);
            }
            vm.set("hiddenfields.currentGroup", isHidden);
        },

        updateValue1FieldVisibility: function () {
            var vm = this.getViewModel();
            var isHidden = true,
                refTextFiledHidden = true;
            var operator = vm.get("values.operator");
            var typeinput = vm.get("values.typeinput");
            var currentGroup = vm.get("values.currentGroup");
            var currentUser = vm.get("values.currentUser");
            if (
                operator &&
                operator !== CMDBuildUI.util.helper.FiltersHelper.operators.null &&
                operator !== CMDBuildUI.util.helper.FiltersHelper.operators.notnull &&
                !typeinput && (!currentGroup && !currentUser)
            ) {
                isHidden = false;
            }

            if (
                [CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference, CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup, CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.activity, CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookupArray].indexOf(vm.get("attributetype")) > -1 &&
                CMDBuildUI.util.helper.FiltersHelper.isOperatorForRefernceOrLookupDescription(operator)
            ) {
                refTextFiledHidden = isHidden;
                isHidden = true;
            }
            vm.set("hiddenfields.value1", isHidden);
            vm.set("hiddenfields.referencetext", refTextFiledHidden);
        },

        updateValue2FieldVisibility: function () {
            var vm = this.getViewModel();
            var isHidden = true;
            var operator = vm.get("values.operator");
            var typeinput = vm.get("values.typeinput");
            var currentGroup = vm.get("values.currentGroup");
            var currentUser = vm.get("values.currentUser");
            if (
                operator &&
                operator === CMDBuildUI.util.helper.FiltersHelper.operators.between &&
                !typeinput && (!currentGroup && !currentUser)
            ) {
                isHidden = false;
            }
            vm.set("hiddenfields.value2", isHidden);
        }
    }
});