Ext.define('CMDBuildUI.view.events.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.events-container',
    listen: {
        global: {
            objectidchanged: 'onObjectIdChanged'
        }
    },
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addevent': {
            click: 'onAddEvent',
            beforerender: 'onAddEventBeforeRender'
        },
        '#statuscombo': {
            change: 'onStatusCategoryDateChange'
        },
        '#contextMenuBtn': {
            beforerender: 'onContextMenuBtnBeforeRender'
        },
        '#categorycombo': {
            change: 'onStatusCategoryDateChange'
        },
        '#datecombo': {
            change: 'onStatusCategoryDateChange'
        },
        '#printPdfBtn': {
            click: 'onPrintBtnClick'
        },
        '#printCsvBtn': {
            click: 'onPrintBtnClick'
        },
        '#savePreferencesBtn': {
            click: 'onSavePreferencesBtnClick'
        }
    },

    onBeforeRender: function () {
        var view = this.getView(),
            vm = this.getViewModel();

        vm.set("title", CMDBuildUI.locales.Locales.calendar.scheduler);

        if (Ext.Object.isEmpty(view.getSchedules())) {

            var preferences = CMDBuildUI.util.helper.UserPreferences.getGridPreferences(
                vm.get("objectType"),
                vm.get("objectTypeName")
            );
            // sorters
            var sorters = [];
            if (preferences && !Ext.isEmpty(preferences.defaultOrder)) {
                preferences.defaultOrder.forEach(function (o) {
                    sorters.push({
                        property: o.attribute,
                        direction: o.direction === "descending" ? "DESC" : 'ASC'
                    });
                });
            } else {
                sorters.push({
                    property: "date",
                    direction: "ASC"
                });
            }

            var schedules = Ext.create('Ext.data.BufferedStore', {
                storeId: 'scheules',
                type: 'buffered',
                model: 'CMDBuildUI.model.calendar.Event',
                autoDestroy: true,
                pageSize: 50,
                leadingBufferZone: 100,
                remoteFilter: true,
                remoteSort: true,
                sorters: sorters,
                proxy: {
                    type: 'baseproxy',
                    url: CMDBuildUI.util.api.Calendar.getEventsUrl(),
                    extraParams: {
                        // detailed: true
                    }
                }
            });

            var advancedFilter = schedules.getAdvancedFilter(),
                statusFilter = this.getStatusBaseFilter();

            advancedFilter.addAttributeFilter(
                statusFilter.attribute,
                statusFilter.operator,
                statusFilter.value,
                statusFilter.attributeId);

            schedules.setAutoLoad(true);
            view.setSchedules(schedules);
        }

        var model = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(vm.get("objectTypeName", vm.get("objectType"))),
            dmsCategoryTypeName = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.category),
            dmsCategoryType = CMDBuildUI.model.dms.DMSCategoryType.getCategoryTypeFromName(dmsCategoryTypeName);

        dmsCategoryType.getCategoryValues().then(function (values) {
            model.set("dmsCategories", values.getRange());
            Ext.Array.forEach(values.getRange(), function (item) {
                item.category = item.get("code");
                item._can_create = true;
            })
        });

        vm.set('datecombostoredata', [{
            value: 0,
            label: CMDBuildUI.locales.Locales.calendar.today,
            localized: {
                label: 'CMDBuildUI.locales.Locales.calendar.today'
            }
        }, {
            value: 7,
            label: CMDBuildUI.locales.Locales.calendar.next7days,
            localized: {
                label: 'CMDBuildUI.locales.Locales.calendar.next7days'
            }
        }, {
            value: 30,
            label: CMDBuildUI.locales.Locales.calendar.next30days,
            localized: {
                label: 'CMDBuildUI.locales.Locales.calendar.next30days'
            }
        }]);

        CMDBuildUI.util.helper.GridHelper.setIconGridPreferences(view);
    },

    onObjectIdChanged: function (eventId) {
        this.getView().setSelectedId(eventId);
        // this.getViewModel().set('selectedId', eventId);
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     * @param {Event} event 
     * @param {Object} eOpts 
     */
    onAddEvent: function () {
        this.redirectTo(
            CMDBuildUI.util.Navigation.getScheduleBaseUrl(
                null,
                CMDBuildUI.mixins.DetailsTabPanel.actions.create
            ));
    },

    onAddEventBeforeRender: function (button, eopts) {
        var session = CMDBuildUI.util.helper.SessionHelper.getCurrentSession(),
            disabled = !session.get('rolePrivileges').calendar_event_create;
        button.setDisabled(disabled);
    },

    /**
    * Filter grid items.
    * @param {Ext.form.field.Text} field
    * @param {Ext.form.trigger.Trigger} trigger
    * @param {Object} eOpts
    */
    onSearchSubmit: function (field, trigger, eOpts) {
        // get value
        var searchTerm = field.getValue();
        if (searchTerm) {
            // add filter
            var view = this.getView(),
                store = view.getSchedules();
            store.getAdvancedFilter().addQueryFilter(searchTerm);
            store.load();
        } else {
            this.onSearchClear(field);
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onContextMenuBtnBeforeRender: function (button, eOpts) {
        this.getView().initContextMenu(button, null, {
            addViewItem: false
        });
    },

    onStatusCategoryDateChange: function () {
        var view = this.getView(),
            store = view.getSchedules();

        if (!store) return;
        var advancedFilter = store.getAdvancedFilter(),
            statusFilter = this.getStatusBaseFilter();
        advancedFilter.removeAttributeFitler('status', 'statusFilter');
        if (statusFilter) {
            advancedFilter.addAttributeFilter(
                statusFilter.attribute,
                statusFilter.operator,
                statusFilter.value,
                statusFilter.attributeId)
        }

        var categoryFilter = this.getCategoryBaseFilter();
        advancedFilter.removeAttributeFitler('category', 'categoryFilter');
        if (categoryFilter) {
            advancedFilter.addAttributeFilter(
                categoryFilter.attribute,
                categoryFilter.operator,
                categoryFilter.value,
                categoryFilter.attributeId)
        }

        var dateFilter = this.getDateBaseFilter();
        advancedFilter.removeAttributeFitler('date', 'dateFilter');
        if (dateFilter) {
            advancedFilter.addAttributeFilter(
                dateFilter.attribute,
                dateFilter.operator,
                dateFilter.value,
                dateFilter.attributeId)
        }
        store.load();
    },

    getStatusBaseFilter: function () {
        var statusValue = this.getView().down('#statuscombo').getValue();

        if (statusValue == null) {
            return {
                attributeId: 'statusFilter',
                attribute: 'status',
                operator: 'in',
                value: ['active', 'expired']
            };
        } else {
            return {
                attributeId: 'statusFilter',
                attribute: 'status',
                operator: 'equal',
                value: statusValue
            }
        }
    },

    getCategoryBaseFilter: function () {
        var view = this.getView(),
            store = view.getSchedules(),
            categoryValue = this.getView().down('#categorycombo').getValue();

        if (store) {
            if (categoryValue == null) {
                return null;
            } else {
                return {
                    attributeId: 'categoryFilter',
                    attribute: 'category',
                    operator: 'equal',
                    value: categoryValue
                }
            }
        }
    },

    getDateBaseFilter: function () {
        var view = this.getView(),
            store = view.getSchedules(),
            dateValue = this.getView().down('#datecombo').getValue();

        if (store) {
            if (dateValue == null) {
                return null;
            } else {
                var nowDate = new Date(),
                    futureDate = Ext.Date.add(nowDate, Ext.Date.DAY, dateValue);

                return {
                    attributeId: 'dateFilter',
                    attribute: 'date',//cahnge
                    operator: 'between',
                    value: [
                        Ext.Date.format(nowDate, 'c'),
                        Ext.Date.format(futureDate, 'c')
                    ]
                }
            }
        }
    },


    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        // clear store filter
        var view = this.getView(),
            store = view.getSchedules();
        store.getAdvancedFilter().clearQueryFilter();
        store.load();
        // reset input
        field.reset();
    },

    /**
    * @param {Ext.form.field.Base} field
    * @param {Ext.event.Event} event
    */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() == event.ENTER) {
            this.onSearchSubmit(field);
        }
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     * @param {Event} event 
     * @param {Object} eOpts 
     */
    onRefreshBtnClick: function (button, event, eOpts) {
        var view = this.getView();
        view.getSchedules().load();
        view.down('events-grid').setSelection();
    },

    /**
     * 
     * @param {Ext.menu.Item} menuitem 
     * @param {Ext.event.Event} event 
     * @param {Object} eOpts 
     */
    onPrintBtnClick: function (menuitem, event, eOpts) {
        var format = menuitem.printformat,
            view = this.getView(),
            store = view.getSchedules(),
            queryparams = {};

        // url and format
        var url = CMDBuildUI.util.api.Calendar.getPrintCalendarsUrl(format);
        queryparams.extension = format;

        // visibile columns
        var columns = view.lookupReference(view.referenceGridId).getVisibleColumns(),
            attributes = [];
        columns.forEach(function (c) {
            if (c.initialConfig.text) {
                attributes.push(c.initialConfig.dataIndex);
            }
        });
        attributes = Ext.Array.difference(attributes, ['Type', 'missingDays', 'completion']);
        queryparams.attributes = Ext.JSON.encode(attributes);

        // apply sorters
        var sorters = store.getSorters().getRange();
        if (sorters.length) {
            queryparams.sort = store.getProxy().encodeSorters(sorters);
        }

        // filters
        var filter = store.getAdvancedFilter();
        if (!(filter.isEmpty() && filter.isBaseFilterEmpty())) {
            queryparams.filter = filter.encode();
        }

        // open file in popup
        CMDBuildUI.util.Utilities.openPrintPopup(url + "?" + Ext.Object.toQueryString(queryparams), format);
    },

    /**
    * 
    * @param {Ext.panel.Tool} tool 
    * @param {Event} event 
    */
    onSavePreferencesBtnClick: function (tool, event) {
        var view = this.getView(),
            vm = view.lookupViewModel(),
            grid = view.lookupReference(view.referenceGridId);
        CMDBuildUI.util.helper.GridHelper.saveGridPreferences(grid, tool, vm.get("objectType"), vm.get("objectTypeName"));
    }

});
