Ext.define('CMDBuildUI.view.administration.content.navigationtrees.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-navigationtrees-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        'domainsClassTree': {
            checkchange: 'onCheckChange'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#addBtn': {
            click: 'onAddBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#enableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onActiveToggleBtnClick'
        }
    },



    /**
     * Before render
     * @param {CMDBuildUI.view.administration.content.navigationtrees.View} view
     */
    onBeforeRender: function (view) {
        view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.navigationtrees.plural);

    },


    onAfterRender: function () {

    },

    /**
     * On translate button click (button, e, eOpts) {
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onTranslateClick: function (button, e, eOpts) {
        var me = this;
        var vm = me.getViewModel();
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfNavigationTreeDescription(vm.get('theNavigationtree').get('name') || '.');
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, CMDBuildUI.util.administration.helper.FormHelper.formActions.add, 'theTranslation', vm, true);
    },

    /**
     * On add navigationtree button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddBtnClick: function (button, e, eOpts) {
        this.redirectTo('administration/navigationtrees_empty/true');
    },

    /**
     * On delete navigationtree button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        var me = this;
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.Ajax.setActionId('delete-navigationtree');
                    me.getViewModel().get('theNavigationtree').erase({
                        success: function (record, operation) {
                            var nextUrl = 'administration/navigationtrees_empty/false';
                            CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy('href', Ext.util.History.getToken(), nextUrl, me);
                        }
                    });
                }
            }, this);
    },


    /**
     * On edit navigationtree button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var vm = this.getView().getViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    /**
     * On cancel button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        if (this.getViewModel().get('actions.edit')) {
            this.redirectTo(Ext.String.format('administration/navigationtrees/{0}', this.getViewModel().get('theNavigationtree.name')), true);
        } else if (this.getViewModel().get('actions.add')) {
            var store = Ext.getStore('administration.MenuAdministration');
            var vm = Ext.getCmp('administrationNavigationTree').getViewModel();
            var currentNode = store.findNode("objecttype", CMDBuildUI.model.administration.MenuItem.types.navigationtree);
            vm.set('selected', currentNode);
            this.redirectTo('administration/navigationtrees_empty/false', true);
        }
    },


    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onActiveToggleBtnClick: function (button, e, eOpts) {
        var me = this;
        var vm = me.getView().getViewModel();
        var theNavigationtree = vm.get('theNavigationtree');
        var treeView = this.getView().down('#domainstree');
        var data = {};
        var node = treeView.getStore().getRootNode();
            if (node.data.checked) {
                if (node.get('root')) {
                    data.description = vm.get('theNavigationtree.description') || vm.get('theNavigationtree.name');
                    data.name = vm.get('theNavigationtree.name');
                    data.active = !theNavigationtree.get('active');
                    data.type = vm.get('theNavigationtree.type');
                    data._id = vm.get('theNavigationtree._id');
                    data.nodes = [{
                        direction: '_1',
                        domain: node.get('domain'),
                        filter: node.get('filter'),
                        nodes: me.collectAllCheckedNodes(node),
                        recursionEnabled: node.get('recursionEnabled'),
                        showOnlyOne: node.get('showOnlyOne'),
                        targetClass: node.get('targetClass')
                    }];
                }
            }
            Ext.Ajax.request({
                url: Ext.String.format('{0}/domainTrees{1}', CMDBuildUI.util.Config.baseUrl, '/' + vm.get('theNavigationtree.name') + '?treeMode=tree' ),
                method: 'PUT',
                jsonData: data,
                success: function (record, transport) {                   
                    me.redirectTo(Ext.String.format('administration/navigationtrees/{0}', me.getViewModel().get('theNavigationtree.name')), true);
                },
                callback: function (reason) {
                    if (button && button.el.dom) {
                        button.setDisabled(false);
                    }
                }
            });
    },

    /**
     * On save button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        var form = this.getView().getForm();
        var vm = this.getView().getViewModel();
        var treeView = this.getView().down('treepanel');

        if (form.isValid()) {
            var data = {};
            var node = treeView.getStore().getRootNode();
            if (node.data.checked) {
                if (node.get('root')) {
                    data.description = vm.get('theNavigationtree.description') || vm.get('theNavigationtree.name');
                    data.name = vm.get('theNavigationtree.name');
                    data.active = vm.get('theNavigationtree.active');
                    data._id = vm.get('theNavigationtree._id');
                    data.nodes = [{
                        direction: '_1',
                        domain: node.get('domain'),
                        filter: node.get('filter'),
                        nodes: me.collectAllCheckedNodes(node),
                        recursionEnabled: node.get('recursionEnabled'),
                        showOnlyOne: node.get('showOnlyOne'),
                        targetClass: node.get('targetClass')
                    }];
                }
            }
            var method = vm.get('theNavigationtree').crudState === 'C' ? 'POST' : 'PUT';
            Ext.Ajax.request({
                url: Ext.String.format('{0}/domainTrees{1}', CMDBuildUI.util.Config.baseUrl, method === 'PUT' ? '/' + vm.get('theNavigationtree.name') + '?treeMode=tree' : ''),
                method: method,
                jsonData: data,
                success: function (transport) {
                    var nextUrl = Ext.String.format('administration/navigationtrees/{0}', vm.get('theNavigationtree.name'));

                    var theTranslation = me.getViewModel().get('theTranslation');
                    if (theTranslation) {
                        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfNavigationTreeDescription(vm.get('theNavigationtree.name'));
                        theTranslation.set('_id', translationCode);
                        theTranslation.crudState = 'U';
                        theTranslation.crudStateWas = 'U';
                        theTranslation.phantom = false;
                        theTranslation.save();
                    }

                    if (vm.get('actions.edit')) {
                        CMDBuildUI.util.administration.MenuStoreBuilder.changeRecordBy('href', nextUrl, vm.get('theNavigationtree').get('description'), me);
                        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                    } else {
                        CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                            function () {
                                var treeComponent = Ext.getCmp('administrationNavigationTree');
                                var treeComponentStore = treeComponent.getStore();
                                var selected = treeComponentStore.findNode("href", nextUrl);
                                treeComponent.setSelection(selected);
                            });
                        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                        me.redirectTo(nextUrl, true);
                    }
                },
                callback: function (reason) {
                    if (button && button.el.dom) {
                        button.setDisabled(false);
                    }
                }
            });
        }
    },

    /**
     * 
     * @param {Ext.data.Model} node the node changed
     * @param {Boolean} checked the value of the checked node
     */
    onCheckChange: function (node, checked) {
        this.recursiveParentCheck(node, checked);
    },
    /**
     * privates
     */
    privates: {

        collectAllCheckedNodes: function (node) {
            var me = this;
            var nodes = [];

            Ext.Array.forEach(node.childNodes, function (child) {
                if (child.get('checked')) {
                    var _node = {
                        filter: child.get('filter'),
                        direction: child.get('direction'),
                        domain: child.get('domain'),
                        showOnlyOne: child.get('showOnlyOne'),
                        recursionEnabled: child.get('recursionEnabled'),
                        targetClass: child.get('targetClass'),
                        nodes: me.collectAllCheckedNodes(child)
                    };
                    nodes.push(_node);
                }
            });
            return nodes;
        }
    }
});