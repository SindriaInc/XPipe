
Ext.define('CMDBuildUI.view.processes.instances.instance.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.processes.instances.instance.ViewController',
        'CMDBuildUI.view.processes.instances.instance.ViewModel'
    ],

    mixins: [
        'CMDBuildUI.view.processes.instances.instance.Mixin',
        'CMDBuildUI.mixins.forms.FormTriggers'
    ],

    alias: 'widget.processes-instances-instance-view',
    controller: 'processes-instances-instance-view',
    viewModel: {
        type: 'processes-instances-instance-view'
    },

    config: {
        buttons: null,
        objectTypeName: null,
        objectId: null,
        activityId: null,
        shownInPopup: false,
        hideTools: false
    },

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.read,
    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

    html: CMDBuildUI.util.helper.FormHelper.waitFormHTML,

    bind: {
        title: '{title}'
    },

    tabpaneltools: CMDBuildUI.view.processes.instances.Util.getTools(),

    /**
     * Render form fields
     */
    showForm: function () {
        var vm = this.getViewModel();

        // attributes configuration from activity
        var attrsConf = this.getAttributesConfigFromActivity();

        // generate tabs/fieldsets and fields
        var items = [];

        var process = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(vm.get("objectTypeName"), vm.get("objectType"));
        var grouping = process.attributeGroups().getRange();
        var activity = vm.get("theActivity");
        var layout;
        if (activity.get("formStructure") && activity.get("formStructure").active && !Ext.isEmpty(activity.get("formStructure").form)) {
            layout = activity.get("formStructure").form;
        }

        var showOnlyAttributesInLayout = activity.get("_definition") === "DUMMY_TASK_FOR_CLOSED_PROCESS" && layout ? true : false;

        if (this.getShownInPopup()) {
            // get form fields as fieldsets
            var formitems = CMDBuildUI.util.helper.FormHelper.renderForm(vm.get("objectModel"), {
                mode: this.formmode,
                attributesOverrides: attrsConf.overrides,
                visibleAttributes: attrsConf.visibleAttributes,
                showAsFieldsets: true,
                grouping: grouping,
                layout: layout,
                activityLinkName: 'theActivity',
                showOnlyAttributesInLayout: showOnlyAttributesInLayout
            });

            // create items
            items = [
                this.getProcessStatusBar(),
                {
                    xtype: 'toolbar',
                    cls: 'fieldset-toolbar',
                    items: this.getCurrentActivityInfo(this.hideTools),
                    margin: 0
                },
                this.getMainPanelForm(formitems)
            ];
        } else {
            // get form fields as tab panel
            var panel = CMDBuildUI.util.helper.FormHelper.renderForm(vm.get("objectModel"), {
                mode: this.formmode,
                showAsFieldsets: false,
                attributesOverrides: attrsConf.overrides,
                visibleAttributes: attrsConf.visibleAttributes,
                grouping: grouping,
                layout: layout,
                activityLinkName: 'theActivity',
                showOnlyAttributesInLayout: showOnlyAttributesInLayout
            });
            Ext.apply(panel, {
                tools: tools
            });
            items.push(panel);
        }
        this.setHtml();
        this.add(items);

        if (this.loadmask) {
            CMDBuildUI.util.Utilities.removeLoadMask(this.loadmask);
        }
    }
});
