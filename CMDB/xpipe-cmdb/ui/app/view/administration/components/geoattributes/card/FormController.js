Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.FormController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-geoattributes-card-form',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },
    onBeforeRender: function () {
        var vm = this.getViewModel();
        var theGeoAttribute = vm.get('theGeoAttribute');
        if (theGeoAttribute && !theGeoAttribute.getAssociatedData().style) {
            theGeoAttribute.set('style', CMDBuildUI.model.map.GeoAttributeStyle.create().getData());
        }
    },

    onAfterRender: function (view) {
        var vm = view.getViewModel();
        if (vm.get('actions.edit')) {
            var nameInput = view.down('[name="name"]');
            if (nameInput) {
                nameInput.vtype = undefined;
            }
        }
    },

    onTreeStoreDataChanged: function (store) {
        var treepanel = this.getView().down('treepanel');
        treepanel.setStore(store);
    },

    /**
     * @param {Ext.form.field.File} input
     * @param {Object} value
     * @param {Object} eOpts
     */
    onFileChange: function (input, value, eOpt) {
        var vm = input.lookupViewModel();
        var file = input.fileInputEl.dom.files[0];
        var reader = new FileReader();

        reader.addEventListener("load", function () {
            vm.get('theGeoAttribute').set('style._iconPath', reader.result);
            input.up().down('#geoAttributeIconPreview').setSrc(reader.result);
        }, false);

        if (file) {
            reader.readAsDataURL(file);
        }
    },

    onTranslateClick: function (event, button, eOpts) {
        var me = this;
        var vm = me.getViewModel();
        var gridVm = vm.get('grid').lookupViewModel();
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfGisAttributeClass(gridVm.get('objectTypeName'), vm.get('theGeoAttribute.name'));
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theDescriptionTranslation', vm, true);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        var me = this;
        var vm = me.getViewModel();
        var form = me.getView();
        if (form.isValid()) {
            var gridVm = vm.get('grid').lookupViewModel();
            var theGeoAttribute = vm.get('theGeoAttribute');

            theGeoAttribute.set('style', theGeoAttribute.getAssociatedData().style);
            if (Ext.isEmpty(theGeoAttribute.get('owner_type'))) {
                theGeoAttribute.set('owner_type', gridVm.get('objectTypeName'));
            }
            if (theGeoAttribute.get('type') !== CMDBuildUI.model.map.GeoAttribute.type.geometry) {
                theGeoAttribute.set('subtype', '');
            }
            delete theGeoAttribute.data.style.id;
            var objectType = gridVm.get('objectType').toLowerCase();

            CMDBuildUI.model.map.GeoAttribute.setProxy({
                url: Ext.String.format('/{0}/{1}/geoattributes', Ext.util.Inflector.pluralize(objectType), vm.get('theGeoAttribute.owner_type')),
                type: 'baseproxy'
            });
            theGeoAttribute.save({
                success: function (record, operation) {
                    me.saveDescriptionTranslation().then(function () {
                        var tabPanel = vm.get('grid').up('tabpanel');
                        var layersVm = tabPanel.down(Ext.String.format('administration-content-{0}-tabitems-layers-layers', Ext.util.Inflector.pluralize(objectType))).getViewModel();
                        if (layersVm && layersVm.getStore('layersStore')) {
                            layersVm.getStore('layersStore').load();
                        }
                        Ext.GlobalEvents.fireEventArgs("geoattributeupdated", [record]);
                        button.setDisabled(false);
                        CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                    });
                },
                failure: function (reason) {
                    button.setDisabled(false);
                }
            });
        } else {
            // TODO: show some message
            button.setDisabled(false);
        }
    },


    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {

        this.getViewModel().get("theGeoAttribute").reject();
        this.getView().up().fireEvent("closed");
    },

    privates: {
        /**
         * @deprecated
         * @private
         * @param {CMDBuildUI.view.administration.content.processes.ViewModel} vm 
         * @param {Function} successCb 
         * @param {Function} errorCb 
         */
        uploadIcon: function (vm, record, button) {
            var me = this;
            CMDBuildUI.util.Ajax.setActionId('geoattribute.icon.upload');
            // define method
            var method = "POST";
            var input = this.lookupReference('iconFile').extractFileInput();


            // init formData
            var formData = new FormData();
            // get url
            var url = Ext.String.format('{0}/uploads?path=images/gis/{1}.png', CMDBuildUI.util.Config.baseUrl, vm.get('theObject.name'));
            // upload 
            CMDBuildUI.util.administration.File.upload(method, formData, input, url, {
                success: function (response) {
                    me.reloadClassesStoreAfterSave(record, button);
                },
                failure: function (error) {
                    me.reloadClassesStoreAfterSave(record, button);
                }
            });

        },

        /**
         * exeute promise for description translation
         */
        saveDescriptionTranslation: function () {
            var deferred = new Ext.Deferred();
            var vm = this.getViewModel();
            var gridVm = vm.get('grid').lookupViewModel();
            var key = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfGisAttributeClass(gridVm.get('objectTypeName'), vm.get('theGeoAttribute.name'));
            // save the translation             
            if (vm.get('theDescriptionTranslation')) {
                vm.get('theDescriptionTranslation').crudState = 'U';
                vm.get('theDescriptionTranslation').crudStateWas = 'U';
                vm.get('theDescriptionTranslation').phantom = false;
                vm.get('theDescriptionTranslation').set('_id', key);
                vm.get('theDescriptionTranslation').save({
                    success: function (translations, operation) {
                        deferred.resolve();
                    }
                });
            } else {
                deferred.resolve();
            }
            return deferred.promise;
        }

    }
});