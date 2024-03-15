Ext.define('CMDBuildUI.view.administration.content.bim.projects.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-bim-projects-view',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function(view){
        Ext.getStore('bim.Projects').load();
    }

});