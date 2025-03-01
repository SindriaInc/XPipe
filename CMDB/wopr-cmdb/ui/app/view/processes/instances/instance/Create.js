
Ext.define('CMDBuildUI.view.processes.instances.instance.Create', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.processes.instances.instance.CreateController',
        'CMDBuildUI.view.processes.instances.instance.CreateModel'
    ],

    mixins: [
        'CMDBuildUI.view.processes.instances.instance.Mixin',
        'CMDBuildUI.mixins.forms.FormTriggers'
    ],

    alias: 'widget.processes-instances-instance-create',
    controller: 'processes-instances-instance-create',
    viewModel: {
        type: 'processes-instances-instance-create'
    },

    modelValidation: true,
    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

    html: CMDBuildUI.util.helper.FormHelper.waitFormHTML,

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.create,

    tabpaneltools: [CMDBuildUI.view.processes.instances.Util.getHelpTool()],

    /**
     * Load activity data and display form.
     * Overrides Mixin loadActivity
     *
     * @param {CMDBuildUI.model.processes.Instance} model
     */
    loadActivity: function (model) {
        var vm = this.getViewModel();

        var activitiesStore = Ext.create("Ext.data.Store", {
            model: 'CMDBuildUI.model.processes.Activity',
            autoLoad: false,
            autoDestroy: true,
            proxy: {
                type: 'baseproxy',
                url: CMDBuildUI.util.api.Processes.getStartActivitiesUrl(
                    vm.get("objectTypeName")
                )
            }
        });

        // load activity and save variables in ViewModel
        activitiesStore.load({
            scope: this,
            callback: function (records, operation, success) {
                if (success && records && records.length) {
                    vm.set("activityId", records[0].getId());
                    vm.set("theActivity", records[0]);
                    // get the process definition
                    var processes = Ext.getStore('processes.Processes');
                    var theProcess = processes.getById(vm.get("objectTypeName"));
                    vm.set("theProcess", theProcess);
                    vm.set('help.text', !Ext.isEmpty(records[0].get('_instructions_translation')) ? records[0].get('_instructions_translation') : theProcess.get('_help_translation') || theProcess.get('help'));
                    // render form
                    this.showForm();
                }
            }
        });
    },

    /**
     * Render form fields
     */
    showForm: function () {
        var me = this;
        var vm = this.getViewModel();

        // attributes configuration from activity
        var attrsConf = this.getAttributesConfigFromActivity();

        // message panel
        var message_panel = this.getMessageBox();

        // action combobox
        var action_field = this.getActionField();

        var process = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(vm.get("objectTypeName"), vm.get("objectType"));
        var grouping = process.attributeGroups().getRange();
        var activity = vm.get("theActivity");
        var layout;
        if (activity.get("formStructure") && activity.get("formStructure").active && !Ext.isEmpty(activity.get("formStructure").form)) {
            layout = activity.get("formStructure").form;
        }

        // get form fields as fieldsets
        var formitems = CMDBuildUI.util.helper.FormHelper.renderForm(vm.get("objectModel"), {
            mode: CMDBuildUI.util.helper.FormHelper.formmodes.create,
            showAsFieldsets: true,
            attributesOverrides: attrsConf.overrides,
            visibleAttributes: attrsConf.visibleAttributes,
            grouping: grouping,
            layout: layout,
            activityLinkName: 'theActivity',
            formAutoValue: process.get('autoValue')
        });

        // add action_field as first element in form items
        Ext.Array.insert(formitems, 0, [message_panel, action_field]);

        this.setHtml();
        this.add(this.getMainPanelForm(formitems));

        // validate form before edit
        Ext.asap(function() {
            me.isValid();
        });
    }

});
