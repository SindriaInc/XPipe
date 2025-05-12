Ext.define('CMDBuildUI.view.navcontent.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.navcontent-container',

    control: {
        '#': {
            clickIconTitle: 'onClickIconTitle'
        }
    },

    listen: {
        global: {
            menunavtreeitemchanged: 'onMenuNavTreeItemChanged'
        }
    },

    updateView: function (currentNode) {
        var view = this.getView();
        view.removeAll();

        // set page title
        if (currentNode.get("_objectdescription")) {
            var label = currentNode.get("_label");
            if (!label) {
                label = CMDBuildUI.util.helper.ModelHelper.getObjectDescription(currentNode.get("_targettypename"));
            }
            view.setTitle(Ext.String.format(
                CMDBuildUI.locales.Locales.main.treenavcontenttitle,
                label,
                currentNode.get("_objectdescription")
            ));
        } else {
            view.setTitle(currentNode.get("text"));
        }

        // add grid
        if (currentNode.get("_targettype") === CMDBuildUI.util.helper.ModelHelper.objecttypes.klass) {
            view.add({
                xtype: 'classes-cards-grid-container',
                objectTypeName: currentNode.get("_targettypename"),
                filter: CMDBuildUI.view.management.navigation.Utils.getFilterForNode(
                    currentNode.get("_navtreedef"),
                    currentNode.get("_objectid"),
                    currentNode.get("_objecttype")
                ),
                header: false,
                maingrid: true
            });
        }
    },

    onMenuNavTreeItemChanged: function (currentNode) {
        var view = this.getView();

        if (currentNode.get("_objectid")) {
            view.setIconCls(Ext.baseCSSPrefix + "fa fa-info-circle");
        } else {
            view.setIconCls();
        }

        this.getViewModel().set("currentNode", currentNode);
        this.updateView(currentNode);

        // Need to write the context because would be empty due to previous clearcontext() call
        CMDBuildUI.util.Navigation.updateCurrentManagementContext(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.navtreecontent,
            view.getNavTreeName()
        );
    },

    onClickIconTitle: function () {
        var currentNode = this.getViewModel().get("currentNode");

        if (currentNode.get("_objectid")) {
            CMDBuildUI.util.Utilities.openPopup(null, currentNode.get("_objectdescription"),
                {
                    xtype: 'classes-cards-card-view',
                    shownInPopup: true,
                    hideTools: true,
                    padding: 10,
                    viewModel: {
                        data: {
                            objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                            objectTypeName: currentNode.get("_objecttype"),
                            objectId: currentNode.get("_objectid")
                        }
                    }
                });
        }
    }

});