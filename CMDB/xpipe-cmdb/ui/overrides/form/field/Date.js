Ext.define('Override.form.field.Date', {
    requires: ['CMDBuildUI.util.helper.UserPreferences', 'CMDBuildUI.util.helper.Configurations'],
    override: 'Ext.picker.Date',

    initComponent: function () {
        var me = this;
        if (CMDBuildUI.util.helper.UserPreferences._preferences) {
            me.startDay = CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.startDay) || CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.fields.startDay) || 0;
        }
        me.callParent(arguments);
    }
});