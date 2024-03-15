Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.domains.DomainsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-domains-tabitems-domains-classes',

    data: {
        actions: {
            view: false,
            edit: false,
            add: false
        },
        toolbarHiddenButtons: {
            edit: true, // action !== view
            print: true // action !== view
        },
        destinationTreeDisabled:  false
    },

    formulas: {
        disableDestinationTree: {
            bind: {
                destinationStore: '{destinationStore}',
                view: '{actions.view}'
            },
            get: function(data){
                var store = data.destinationStore;
                if(!store.getRoot().get('children') || this.get('actions.view')){
                    this.set('destinationTreeDisabled', true);
                }else{
                    this.set('destinationTreeDisabled', false);
                }
            }
        },
        disableOriginTree: {
            bind: {
                originStore: '{originStore}',
                view: '{actions.view}'
            },
            get: function(data){
                var store = data.originStore;
                if(!store.getRoot().get('children') || this.get('actions.view')){
                    this.set('originTreeDisabled', true);
                }else{
                    this.set('originTreeDisabled', false);
                }
            }
        },
        action: {
            bind: '{theObject}',
            get: function (get) {
                if (this.get('actions.edit')) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (this.get('actions.add')) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        },
        getToolbarButtons: {
            bind: '{theObject.active}',
            get: function (get) {
                this.set('toolbarHiddenButtons.edit', !this.get('actions.view'));
                this.set('toolbarHiddenButtons.print', !this.get('actions.view'));
            }
        }
    },

    stores: {
        originStore: {
            type: 'tree',
            proxy: {
                type: 'memory'
            },
            fields: ['description','enabled'],
            root: {
                expanded: true
            },
            autoDestroy: true
        },
        destinationStore: {
            type: 'tree',
            proxy: {
                type: 'memory'
            },
            fields: ['description','enabled'],
            root: {
                expanded: true
            },
            autoDestroy: true
        }
    }
});
