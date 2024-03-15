Ext.define('CMDBuildUI.view.graph.topMenu.TopMenuController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.graph-topmenu-topmenu',
    listen: {
        component: {
            '#reopengraph': {
                click: 'onReopenGraphButtonClick'
            },
            'menucheckitem': {
                checkchange: 'onCheckItemsChange'
            },
            '#refresh': {
                click: 'onRefreshButtonClick'
                // beforerender: 'onRefreshBeforeRender'
            }
        }
    },

    // TODO: move to GraphContainerController
    /**
     * @param {Ext.panel.Tool} tool
     * @param {Ext.event.Event} e
     * @param {Ext.Component} owner
     * @param {Object} eOpts
     */
    onReopenGraphButtonClick: function (tool, e, owner, eOpts) {
        var selNode = this.getViewModel().get('selectedNode')[0],
            dataNode = this.getViewModel().get('cy').nodes('#' + selNode.id).data(),
            tmpSelectedNode = {
                type: dataNode.type,
                id: dataNode.id,
                code: dataNode.code,
                description: dataNode.description
            },
            view = tool ? tool : this.getView(),
            vmGraph = view.up('graph-graphcontainer').getViewModel();

        vmGraph.set("pointerExternalCanvas", false);
        this.graphResetEnvironment(tmpSelectedNode);
    },

    /**
     * @param {[Object]} tmpSelectedNode 
     * {    id: id,
     *      type: targetClass
     * }
     */
    graphResetEnvironment: function (tmpSelectedNode) {
        var graphView = this.getView().up('graph-graphcontainer');
        var graphController = graphView.getController();

        graphController.resetEnvironment(tmpSelectedNode); //FIXME: no argument in the function required
    },

    /**
     * @param {Ext.menu.CheckItem} item
     * @param {Boolean} checked
     * @param {Object} eOpts
     */
    onCheckItemsChange: function (item, checked, eOpts) {
        var view = this.getView().down('#chooseNavTree');
        var menu = view.getMenu();
        var checkItems = menu.items.items; //Can use getrefItems

        checkItems.forEach(function (checkItem) {
            if (checkItem.name != item.name) {
                checkItem.setChecked(false, true); //suspendEvent true
            }
        }, this);
    },

    // TODO: move to GraphContainerController
    /**
     * @param {Ext.panel.Tool} tool
     * @param {Ext.event.Event} e
     * @param {Ext.Component} owner
     * @param {Object} eOpts
     */
    onRefreshButtonClick: function (tool, e, owner, eOpts) {
        var viewContainer = tool.up('graph-graphcontainer');
        var originalNode = {
            type: viewContainer._type, //class name
            id: viewContainer._id, //card id
            code: viewContainer._code,
            description: viewContainer._description
        };

        viewContainer.getViewModel().set("pointerExternalCanvas", false);
        this.graphResetEnvironment(originalNode);

        // onReopenGraphButtonClick/
    }

});
