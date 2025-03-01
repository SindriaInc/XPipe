Ext.define('CMDBuildUI.view.administration.content.dashboards.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    requires: ['CMDBuildUI.util.administration.helper.ConfigHelper'],
    alias: 'controller.administration-content-dashboards-card-viewinrow',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onViewBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onActiveToggleBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.classes.tabitems.dashboards.card.ViewInRow} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        this.linkDashboard(view, vm);
        view.add(CMDBuildUI.view.administration.content.dashboards.card.FormHelper.getGeneralProperties('display'));
        view.add(CMDBuildUI.view.administration.content.dashboards.card.FormHelper.getLayout('display'));
        view.setActiveTab(0);
    },

    onEditBtnClick: function () {
        var view = this.getView(),
            viewModel = {
                data: {
                    theDashboard: this.getViewModel().get('theDashboard'),
                    grid: view.up('grid'),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    }
                }
            };
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'view-administration-content-dashboards-card',
            viewModel: viewModel
        });
    },

    onViewBtnClick: function () {
        var view = this.getView(),
            viewModel = {
                data: {
                    theDashboard: this.getViewModel().get('theDashboard'),
                    grid: view.up('grid'),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            };
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'view-administration-content-dashboards-card',
            viewModel: viewModel
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onActiveToggleBtnClick: function (button, e, eOpts) {
        var me = this;
        var view = me.getView();
        var vm = view.getViewModel();
        var theDashboard = vm.get('theDashboard');
        theDashboard.set('active', !theDashboard.get('active'));
        theDashboard.save({
            success: function (record, operation) {
                var ctx = view._rowContext;
                ctx.ownerGrid.fireEventArgs('itemupdated', [theDashboard]);
            }
        });

    },
    onDeleteBtnClick: function (button, event) {
        var me = this,
            view = me.getView(),
            ctx = view._rowContext;
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    if (ctx.record.isModel) {
                        CMDBuildUI.util.Ajax.setActionId(Ext.String.format('delete-{0}', ctx.record.store.model.objectTypeName));
                    } else {
                        CMDBuildUI.util.Ajax.setActionId(Ext.String.format('delete-{0}', 'unknowObjectName'));
                    }
                    ctx.record.erase({
                        success: function (record, operation) {
                            var nextIndex = ctx.record.store.isLast(ctx.record) ? (ctx.recordIndex - 1) < 0 ? null : ctx.recordIndex - 1 : ctx.recordIndex + 1;
                            ctx.ownerGrid.fireEventArgs('itemdeleted', [nextIndex]);
                        }
                    });
                }
            }, this);
    },
    linkDashboard: function (view, vm) {
        if (view) {
            Ext.asap(function () {
                try {
                    view.mask('loading');
                } catch (error) { }
            });
            var selected = view._rowContext.record;
            vm.bind({
                bindTo: {
                    theDashboard: '{theDashboard}'
                }
            }, function (data) {
                if (view.masked) {
                    view.unmask();
                }
            });
            vm.linkTo('theDashboard', {
                type: 'CMDBuildUI.model.dashboards.Dashboard',
                id: selected.get('_id')
            });
        }
    }


});