Ext.define('CMDBuildUI.util.bim.Util', {
    singleton: true,

    /**
     * @param {String} GlobalId
     * @param {Function} callback The function to execute in success case
     * @param {Object} scope The scope for the function
     */
    getRelatedCard: function (projectId, globalId, callback, scope) {
        Ext.Ajax.request({
            url: Ext.String.format('{0}/bim/projects/{1}/values/{2}?{3}',
                CMDBuildUI.util.Config.baseUrl,
                projectId,
                globalId,
                "if_exists=true"
            ),

            method: 'GET',
            success: function (response) {
                var data = JSON.parse(response.responseText).data;

                callback.call(scope || this, data)
            }
        })
    },

    /**
     * 
     * @param {*} projectId 
     * @param {*} selectedId 
     */
    openBimPopup: function (typeViewer, projectId, selectedId) {

        function openPopup(xtype) {
            CMDBuildUI.util.Utilities.openPopup('bimPopup', CMDBuildUI.locales.Locales.bim.bimViewer, {
                xtype: xtype,
                projectId: projectId,
                selectedId: selectedId
            });
        }

        if (typeViewer === "xeokit") {
            openPopup('bim-xeokit-container');
        } else {
            CMDBuildUI.util.Ajax.setActionId("class.card.bim.open");
            Ext.Loader.loadScript({
                url: ['resources/js/viewer/bimsurfer.js', 'resources/js/viewer/cmdbuildBimViewer.bundle.js'],
                onLoad: function () {
                    openPopup('bim-bimserver-container');
                }
            });
        }
    }
});