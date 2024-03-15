Ext.define('CMDBuildUI.view.administration.content.gis.gismenus.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-gismenus-view',

    control: {        
        '#editBtn': {
            click: 'onEditBtnClick'
        }
    },

   
    /**
     * 
     * @param {*} button 
     * @param {*} event 
     * @param {*} eOpts 
     */
    onEditBtnClick: function (button, event, eOpts) {
        this.getViewModel().setCurrentAction(CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },    

    privates: {
        generateNewIds: function (item) {
            var me = this;
            if (item.menuType !== 'root') {
                item._id = CMDBuildUI.util.Utilities.generateUUID();
            }
            if (item.children && item.children.length) {
                Ext.Array.forEach(item.children, function (_item, index) {
                    item.children[index] = me.generateNewIds(_item);
                });
            }
            return item;
        }
    }
});