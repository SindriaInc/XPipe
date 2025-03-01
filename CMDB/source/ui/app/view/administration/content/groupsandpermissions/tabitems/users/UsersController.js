Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.users.UsersController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-groupsandpermissions-tabitems-users-users',

    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#usersassignedgrid': {
            select: 'onUserAssignedSelect'
        },
        '#usersunassignedgrid': {
            select: 'onUserUnassignedSelect'
        }
    },

    onAfterRender: function (view) {
        this.toggleDragDrop('disable');
    },

    onUserAssignedSelect: function () {
        this.getView().down('#usersunassignedgrid').setSelection(null);
    },

    onUserUnassignedSelect: function () {
        this.getView().down('#usersassignedgrid').setSelection(null);
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var vm = this.getView().up('administration-content-groupsandpermissions-view').getViewModel();
        vm.set('actions.view', false);
        vm.set('actions.edit', true);
        vm.set('actions.add', false);
        vm.toggleEnableTabs(2);
        this.toggleDragDrop('enable');
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        var vm = this.getView().up('administration-content-groupsandpermissions-view').getViewModel();
        button.setDisabled(true);
        var cancelBtn = this.getView().lookupReference('groupusers_cancelbtn');
        cancelBtn.setDisabled(true);

        var assignedGrid = this.getView().lookupReference('usersassignedgrid').getView();
        var unassignedGrid = this.getView().lookupReference('usersunassignedgrid').getView();
        assignedGrid.mask(CMDBuildUI.locales.Locales.administration.common.messages.saving);

        var assignedStore = assignedGrid.getStore();
        var unassignedStore = unassignedGrid.getStore();
        var jsonData = {
            add: [],
            remove: []
        };

        Ext.Array.forEach(assignedStore.getNewRecords(), function (element) {
            var found = Ext.Array.findBy(assignedStore.originalRecords, function (originalRecord) {
                return originalRecord.get('_id') === element.get('_id');
            });

            if (!found) {
                jsonData.add.push(element.get('_id'));
            }

        });
        Ext.Array.forEach(assignedStore.getRemovedRecords(), function (element) {
            jsonData.remove.push(element.get('_id'));
        });
        jsonData.add = Ext.Array.unique(jsonData.add);
        jsonData.remove = Ext.Array.unique(jsonData.remove);
        Ext.Ajax.request({
            url: CMDBuildUI.util.Config.baseUrl + '/roles/' + vm.get('theGroup._id') + '/users',
            method: 'PUT',
            jsonData: jsonData,
            /**
             * 
             * @param {Object} options 
             * @param {Boolean} success 
             * @param {Object} response 
             */
            callback: function (options, success, response) {
                if (success) {
                    vm.set('actions.view', true);
                    vm.set('actions.edit', false);
                    vm.set('actions.add', false);
                    vm.toggleEnableTabs();
                    me.toggleDragDrop('disable');
                    assignedStore.load();
                    unassignedStore.load();
                }
                try {
                    button.setDisabled(false);
                    cancelBtn.setDisabled(false);
                    assignedGrid.unmask();
                    assignedGrid.setSelection(null);
                    unassignedGrid.setSelection(null);                    
                } catch (e) {}
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = this.getView().up('administration-content-groupsandpermissions-view').getViewModel();
        vm.get('theGroup').reject();
        vm.set('actions.view', true);
        vm.set('actions.edit', false);
        vm.set('actions.add', false);
        this.toggleDragDrop('disable');        
        this.getView().lookupReference('usersassignedgrid').getStore().reload();
        this.getView().lookupReference('usersunassignedgrid').getStore().reload();
        vm.toggleEnableTabs();
    },

    privates: {
        /**
         * @private
         * @param {String} action 
         */
        toggleDragDrop: function (action) {
            var assignedGrid = this.getView().lookupReference('usersassignedgrid').getView().getPlugin('dragdrop');
            var unassignedGrid = this.getView().lookupReference('usersunassignedgrid').getView().getPlugin('dragdrop');
            switch (action) {
                case 'enable':
                    assignedGrid.enable();
                    unassignedGrid.enable();
                    break;
                case 'disable':
                    assignedGrid.disable();
                    unassignedGrid.disable();
                    break;
            }

        }
    }

});