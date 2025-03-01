Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.domains.ClassesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-domains-tabitems-domains-classes',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.administration.content.domains.tabitems.domains.Classes} view 
     */
    onBeforeRender: function (view) {
        var vm = view.getViewModel();
        var theObject;

        vm.bind({
            bindTo: {
                source: '{theDomain.source}',
                destination: '{theDomain.destination}'
            }
        }, function (data) {
            if (data.source && data.destination) {
                theObject = vm.get('theDomain') || vm.getData().theDomain;


                var classStore = Ext.create('Ext.data.ChainedStore', {
                    source: 'classes.Classes'
                });

                var processStore = Ext.create('Ext.data.ChainedStore', {
                    source: 'processes.Processes'
                });

                // source can be class or process
                var source = (!theObject.get('sourceProcess')) ?
                    classStore.getById(theObject.get('source')) :
                    processStore.getById(theObject.get('source'));

                // destination can be domain or process
                var destination = (!theObject.get('destinationProcess')) ?
                    classStore.getById(theObject.get('destination')) :
                    processStore.getById(theObject.get('destination'));

                // set source checkbox checked or not
                var sourceTree = [];
                if (source) {
                    sourceTree = source.getChildrenAsTree(true, function (item) {
                        item.set('enabled', theObject.get('disabledSourceDescendants').indexOf(item.get('name')) === -1);
                        return item;
                    });
                }

                // set destination checkbox checked or not
                var destinationTree = [];
                if (destination) {
                    destinationTree = destination.getChildrenAsTree(true, function (item) {
                        item.set('enabled', theObject.get('disabledDestinationDescendants').indexOf(item.get('name')) === -1);
                        return item;
                    });
                }

                // generate the source tree
                var originRoot = {
                    expanded: true,
                    text: theObject.getSourceDescription(),
                    name: source ? source.get('name') : theObject.get('source'),
                    leaf: sourceTree.length >= 1 ? false : true,
                    children: sourceTree.length >= 1 ? sourceTree : false,
                    enabled: !sourceTree.length ? true : sourceTree.length == 1 ? (sourceTree[0].enabled) ? true : false : undefined
                };
                vm.get('originStore').setRoot(originRoot);

                // generate the destination tree
                var destinationRoot = {
                    expanded: true,
                    text: theObject.getDestinationDescription(),
                    name: destination ? destination.get('name') : theObject.get('destination'),
                    leaf: destinationTree.length >= 1 ? false : true,
                    children: destinationTree.length >= 1 ? destinationTree : false,
                    enabled: !destinationTree.length ? true : destinationTree.length == 1 ? (destinationTree[0].enabled) ? true : false : undefined
                };
                vm.get('destinationStore').setRoot(destinationRoot);
                vm.set('action', vm.get('action'));
            }
        });

    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        this.getViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        var vm = button.up('administration-content-domains-tabitems-domains-classes').getViewModel();
        var theObject = vm.get('theDomain');
        theObject.save({
            success: function (batch, options) { }
        });
        var nextUrl = Ext.String.format('administration/domains/{0}', theObject.get('name'));
        CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
            function () {
                var treeComponent = Ext.getCmp('administrationNavigationTree');
                var treeComponentStore = treeComponent.getStore();
                var selected = treeComponentStore.findNode("href", nextUrl);

                treeComponent.setSelection(selected);
            });
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        me.redirectTo(nextUrl, true);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var tabView = button.up('administration-content-domains-tabitems-domains-classes');
        var vm = tabView.getViewModel();
        var nextUrl = Ext.String.format('administration/domains/{0}', vm.get('theDomain.name'));
        this.redirectTo(nextUrl, true);
    }
});