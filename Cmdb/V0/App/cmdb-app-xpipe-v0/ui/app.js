/*
 * This file is generated and updated by Sencha Cmd. You can edit this file as
 * needed for your application, but these edits will have to be merged by
 * Sencha Cmd when upgrading.
 */
Ext.application({
    name: 'CMDBuildUI',

    extend: 'CMDBuildUI.Application',

    requires: [
        'CMDBuildUI.view.main.Main'
    ],

    // The name of the initial view to create. With the classic toolkit this class
    // will gain a "viewport" plugin if it does not extend Ext.Viewport. With the
    // modern toolkit, the main view will be added to the Viewport.
    //
    // mainView: 'CMDBuildUI.view.main.Main',

    //-------------------------------------------------------------------------
    // Most customizations should be made to CMDBuildUI.Application. If you need to
    // customize this file, doing so below this section reduces the likelihood
    // of merge conflicts when upgrading to new versions of Sencha Cmd.
    //-------------------------------------------------------------------------

    listen: {
        controller: {
            '#': {
                unmatchedroute: 'onUnmatchedRoute'
            }
        }
    },

    onUnmatchedRoute: function () {
        var token = Ext.History.getToken();
        var stoken = token.split("/");
        var newstoken = Ext.Array.slice(stoken, 0, stoken.length - 1);
        var newtoken = newstoken.join("/");
        if (Ext.isEmpty(newtoken)) {
            newtoken = "management";
        }
        CMDBuildUI.util.Utilities.redirectTo(newtoken);
    }
});
