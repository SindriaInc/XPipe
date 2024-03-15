Ext.define('CMDBuildUI.view.map.tab.cards.CardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-tab-cards-card',
    listen: {
        global: {
            'refreshCard': 'onRefreshBtnClick'
        },
        component: {
            '#': {
                'beforerender': 'onBeforeRender',
                'refreshbtnclick': 'onRefreshBtnClick'
            }
        }
    },

    /**
      * @param {CMDBuildUI.view.classes.cards.Grid} view
      * @param {Object} eOpts
      */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        vm.bind({
            theObject: '{map-tab-tabpanel.theObject}'
        }, function (data) {
            var view = this.getView();

            if (data.theObject) {
                view.refreshTheObject(data.theObject)
            } else {
                view.remove('classes-cards-card-view');
            }
        }, this);
    },

    /**
     * 
     * @param {Ext.tab.Panel} tabpanel 
     * @param {Boolean} keepSelection 
     */
    onRefreshBtnClick: function (tabpanel, keepSelection) {
        var theObject = tabpanel.getTheObject();

        if (theObject) {
            this.getView().refreshTheObject(theObject);
            if (!keepSelection) {
                tabpanel.getListTab().setSelection();
            }
        }
    }
});
