Ext.define("CMDBuildUI.components.calendar.Calendar", {
    extend: 'Ext.panel.Panel',
    alias: 'widget.ux-calendar',

    config: {
        /**
         * @cfg {Boolean} allowViewDay
         * Allow daily view. Default `true`.
         */
        allowViewDay: true,

        /**
         * @cfg {Boolean} allowViewWeek
         * Allow weekly view. Default `true`.
         */
        allowViewWeek: true,

        /**
         * @cfg {Boolean} allowViewMonth
         * Allow monthly view. Default `true`.
         */
        allowViewMonth: true,

        /**
         * @cfg {Boolean} allowViewAgenda
         * Allow monthly view. Default `true`.
         */
        allowViewAgenda: true,

        /**
         * @cfg {Boolean} allowPrevNextYear
         * Allow buttons to navigate to next or previous year. Default `true`.
         */
        allowPrevNextYear: true,

        /**
         * @cfg {String} defaultView
         * One of `day`, `week`, `month`, `agenda`. Default is `month`.
         */
        defaultView: 'month',

        /**
         * @cfg {String || String[]} dataSourceType
         */
        dataSourceType: null,

        /**
         * @cfg {String || String[]} dataSourceTypeName
         */
        dataSourceTypeName: null,

        /**
         * @cfg {String || String[]} dataSourceUrl
         */
        dataSourceUrl: null,

        /**
         * @cfg {Object || Object[]} dataSourceFilter
         * eCQL filter
         */
        dataSourceFilter: null,

        /**
         * @cfg {Object || Object[]} dataSourceExtraParams
         * eCQL filter
         */
        dataSourceExtraParams: null,

        /**
         * @cfg {CMDBuild.model.Base || CMDBuild.model.Base[]} targetObject
         */
        targetObject: null,

        /**
         * @cfg {String || String[]} eventStartDateAttribute
         */
        eventStartDateAttribute: null,

        /**
         * @cfg {String || String[]} eventEndDateAttribute
         */
        eventEndDateAttribute: null,

        /**
         * @cfg {Boolean} eventDatesUseTime
         * `true` to take the time to use for creating events on the calendar.
         * Otherwise the events will have 00:00 as start and end time.
         */
        eventDatesUseTime: false,

        /**
         * @cfg {String || String[]} eventTitleAttribute
         * Default is `Description`.
         */
        eventTitleAttribute: "Description",

        /**
         * @cfg {String || String[]} eventTypeLookup
         * The LookUp type to use for type.
         * The lookup icon color is used as event color. If not specifed the default is the primary color.
         * The lookup text color is used as event text color. If not specifed the default is white.
         */
        eventTypeLookup: null,

        /**
         * @cfg {String || String[]} eventLookupValueField
         * The LookUp property used as value field. One of `_id` and `code`.
         */
        eventLookupValueField: '_id',

        /**
         * @cfg {String || String[]} eventTypeAttribute
         */
        eventTypeAttribute: null,

        /**
         * @cfg {Function} eventClickHandler
         */
        eventClickHandler: null,

        /**
         * @cfg {String|Date} openingDate
         */
        openingDate: null,

        /**
         * @cfg {Function} viewSkeletonRender
         */
        viewSkeletonRender: null
    },

    layout: 'fit',
    bodyPadding: 10,
    scroller: true,

    baseCls: Ext.baseCSSPrefix + 'ux-calendar',

    onBoxReady: function (width, height) {
        var me = this;
        this.callParent(arguments);

        this._currentstate = [];
        Ext.Array.forEach(this.getDataSourceType(), function (item, index, allitems) {
            var indexTitle = index < me.getEventTitleAttribute().length ? index : 0;
            me._currentstate.push({
                start: null,
                end: null,
                titleattr: me.getEventTitleAttribute()[indexTitle],
                startdateattr: me.getEventStartDateAttribute()[index],
                enddateattr: me.getEventEndDateAttribute() && me.getEventEndDateAttribute()[index] ? me.getEventEndDateAttribute()[index] : me.getEventStartDateAttribute()[index],
                typeattr: me.getEventTypeAttribute() ? me.getEventTypeAttribute()[index] : null
            })
        });

        // generate data store
        Ext.Loader.loadScript({
            url: [
                "resources/js/fullcalendar/core/main.min.js",
                "resources/js/fullcalendar/daygrid/main.min.js",
                "resources/js/fullcalendar/interaction/main.min.js",
                "resources/js/fullcalendar/list/main.min.js",
                "resources/js/fullcalendar/locales/locales-all.min.js",
                "resources/js/fullcalendar/timegrid/main.min.js"
            ],
            onLoad: function () {
                Ext.Promise.all([
                    me.initStore(),
                    me.initCategories()
                ]).then(function () {
                    me.initCalendar();
                });
            }
        });
    },

    /**
     * Custom init component
     */
    initComponent: function () {
        this.dataSourceType = Ext.Array.from(this.dataSourceType);
        this.dataSourceTypeName = Ext.Array.from(this.dataSourceTypeName);
        this.dataSourceUrl = Ext.Array.from(this.dataSourceUrl);
        this.dataSourceFilter = Ext.Array.from(this.dataSourceFilter);
        this.dataSourceExtraParams = Ext.Array.from(this.dataSourceExtraParams);
        this.targetObject = Ext.Array.from(this.targetObject);
        this.eventStartDateAttribute = Ext.Array.from(this.eventStartDateAttribute);
        this.eventEndDateAttribute = Ext.Array.from(this.eventEndDateAttribute);
        this.eventTitleAttribute = Ext.Array.from(this.eventTitleAttribute);
        this.eventTypeLookup = Ext.Array.from(this.eventTypeLookup);
        this.eventLookupValueField = Ext.Array.from(this.eventLookupValueField);
        this.eventTypeAttribute = Ext.Array.from(this.eventTypeAttribute);
        this.callParent();
    },

    /**
     * Initialize calendar
     */
    initCalendar: function () {
        var me = this,
            rightbtns = [],
            leftbtns = "prev,today,next",
            startdate = this.getOpeningDate() || new Date();

        // configure right buttons
        if (this.getAllowViewMonth()) {
            rightbtns.push("dayGridMonth");
        }
        if (this.getAllowViewWeek()) {
            rightbtns.push("timeGridWeek");
        }
        if (this.getAllowViewDay()) {
            rightbtns.push("timeGridDay");
        }
        if (this.getAllowViewAgenda()) {
            rightbtns.push("listWeek");
        }

        // configure left buttons
        if (this.getAllowPrevNextYear()) {
            leftbtns = 'prevYear,' + leftbtns + ',nextYear';
        }

        var calendarEl = Ext.dom.Helper.append(
            this.body,
            '<div></div>'
        );

        this.calendar = new FullCalendar.Calendar(calendarEl, {
            plugins: ['dayGrid', 'timeGrid', 'list', 'interaction'],
            header: {
                left: leftbtns,
                center: 'title',
                right: rightbtns.join(",")
            },
            height: this.body.getHeight() - (this.bodyPadding * 2),
            defaultDate: startdate,
            navLinks: false, // can click day/week names to navigate views
            editable: false,
            eventLimit: true, // allow "more" link when too many events
            locale: CMDBuildUI.util.helper.SessionHelper.getLanguage(),
            firstDay: CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.startDay) || CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.fields.startDay) || 0,
            datesRender: function (info) {
                me.loadEvents(info.view.activeStart, info.view.activeEnd);
            },
            viewSkeletonRender: me.viewSkeletonRender,

            eventClick: function (eventClickInfo) {
                var f = me.eventClickHandler || me.defaultEventClickHandler;
                f.call(me, eventClickInfo);
            }
        });

        this.calendar.render();
    },

    /**
     *
     * @param {CMDBuildUI.util.AdvancedFilter} advancedFilter
     */
    applyAdvanceFilter: function (advancedFilter) {
        if (this._currentstate.length === 1) {
            var store = this._currentstate[0].store;

            // set default dataSource eCql filter
            if (!Ext.isEmpty(this.getDataSourceFilter()) && !Ext.isEmpty(this.getTargetObject())) {
                advancedFilter.baseFilter.ecql = CMDBuildUI.util.ecql.Resolver.resolve(this.getDataSourceFilter()[0], this.getTargetObject()[0]);
            }

            store.setAdvancedFilter(advancedFilter);
            this.onAdvancedFilterChange();
        } else {
            CMDBuildUI.util.Logger.log(
                CMDBuildUI.locales.Locales.calendar.execapplyfilter,
                CMDBuildUI.util.Logger.levels.warn
            );
        }
    },

    /**
     * Removes the advanced filter
     */
    removeAdvanceFilter: function () {
        if (this._currentstate.length === 1) {
            var store = this._currentstate[0].store;

            // set default dataSource eCql filter
            if (!Ext.isEmpty(this.getDataSourceFilter()) && !Ext.isEmpty(this.getTargetObject())) {
                advancedFilter.baseFilter.ecql = CMDBuildUI.util.ecql.Resolver.resolve(this.getDataSourceFilter()[0], this.getTargetObject()[0]);
            }

            store.setAdvancedFilter(null);
            this.onAdvancedFilterChange()
        } else {
            CMDBuildUI.util.Logger.log(
                CMDBuildUI.locales.Locales.calendar.execremovefilter,
                CMDBuildUI.util.Logger.levels.warn
            );
        }
    },

    privates: {
        /**
         *
         * @param {Date} start
         * @param {Date} end
         */
        loadEvents: function (start, end) {
            var me = this,
                requests = [];

            CMDBuildUI.util.Utilities.showLoader(true, me);

            Ext.Array.forEach(this.getDataSourceType(), function (item, index, allitems) {
                var startCalc = false,
                    endCalc = false;

                !me._currentstate[index].start || Ext.Date.diff(me._currentstate[index].start, start, Ext.Date.DAY) < 0 ? startCalc = true : startCalc = false;
                !me._currentstate[index].end || Ext.Date.diff(me._currentstate[index].end, end, Ext.Date.DAY) > 0 ? endCalc = true : endCalc = false;

                if (startCalc || endCalc) {
                    var deferred = new Ext.Deferred();

                    startCalc ? me._currentstate[index].start = start : null;
                    endCalc ? me._currentstate[index].end = end : null;

                    // set store filter
                    var advancedFilter = me._currentstate[index].store.getAdvancedFilter();
                    if (me._currentstate[index].startdateattr === me._currentstate[index].enddateattr) {
                        advancedFilter.removeAttributeFitler(me._currentstate[index].startdateattr)

                        advancedFilter.addAttributeFilter(
                            me._currentstate[index].startdateattr,
                            CMDBuildUI.util.helper.FiltersHelper.operators.between,
                            [me._currentstate[index].start.toISOString(), me._currentstate[index].end.toISOString()]
                        );

                    } else {
                        advancedFilter.removeAttributeFitler(me._currentstate[index].startdateattr)
                        advancedFilter.addAttributeFilter(
                            me._currentstate[index].startdateattr,
                            CMDBuildUI.util.helper.FiltersHelper.operators.less,
                            me._currentstate[index].end.toISOString()
                        );

                        advancedFilter.removeAttributeFitler(me._currentstate[index].enddateattr)
                        advancedFilter.addAttributeFilter(
                            me._currentstate[index].enddateattr,
                            CMDBuildUI.util.helper.FiltersHelper.operators.greater,
                            me._currentstate[index].start.toISOString()
                        );
                    }

                    // load store
                    me._currentstate[index].store.load({
                        callback: function (records, operation, success) {
                            // remove events
                            me.removeAllEvents(me.getDataSourceTypeName()[index]);

                            records.forEach(function (record) {
                                me.addEvent(record, index);
                            });

                            deferred.resolve();
                        },
                        addRecords: true
                    });

                    requests.push(deferred);
                }
            });

            Ext.Promise.all(requests).then(function () {
                CMDBuildUI.util.Utilities.showLoader(false, me);
            })
        },

        /**
         *
         * @param {String} typename
         */
        removeAllEvents: function (typename) {
            this.calendar.getEvents().forEach(function (event) {
                if (typename === event.extendedProps.type) {
                    event.remove();
                }
            });
        },

        /**
         *
         * @param {CMDBuild.model.classes.Card|CMDBuild.model.processes.Instance} record
         * @param {Number} index
         */
        addEvent: function (record, index) {
            var dateformat = 'Y-m-d';
            if (this.getEventDatesUseTime()) {
                dateformat = 'Y-m-d H:i';
            }

            var event = {
                id: record.getId(),
                start: Ext.Date.format(record.get(this._currentstate[index].startdateattr), dateformat),
                end: Ext.Date.format(record.get(this._currentstate[index].enddateattr), dateformat),
                title: record.get(this._currentstate[index].titleattr),
                classNames: ['event-cursor-pointer'],
                type: this.getDataSourceTypeName()[index],
                _objecttypename: record.get("_type")
            };

            if (this._currentstate[index].eventtypes && record.get(this._currentstate[index].typeattr)) {
                var indexValue = index < this.getEventLookupValueField().length ? index : 0,
                    lv = this._currentstate[index].eventtypes.findRecord(
                        this.getEventLookupValueField()[indexValue],
                        record.get(this._currentstate[index].typeattr)
                    );
                if (lv) {
                    if (lv.get("icon_color")) {
                        event.backgroundColor = lv.get("icon_color");
                        event.borderColor = lv.get("icon_color");
                    }
                    if (lv.get("text_color")) {
                        event.textColor = lv.get("text_color");
                    }
                }
            }
            this.calendar.addEvent(event);
        },

        /**
         * Load classes store.
         *
         * @return {Ext.promise.Promise}
         */
        initStore: function () {
            var me = this,
                def = new Ext.Deferred(),
                requests = [];

            Ext.Array.forEach(this.getDataSourceType(), function (item, index, allitems) {
                var deferred = new Ext.Deferred();
                if (me.getDataSourceType()[index] && me.getDataSourceTypeName()[index] && me.getEventStartDateAttribute()[index]) {
                    var state = me._currentstate[index],
                        attrs = ['_id', '_type'];
                    if (state.enddateattr) {
                        attrs.push(state.enddateattr);
                    }
                    if (state.startdateattr) {
                        attrs.push(state.startdateattr);
                    }
                    if (state.titleattr) {
                        attrs.push(state.titleattr);
                    }
                    if (state.typeattr) {
                        attrs.push(state.typeattr);
                    }
                    CMDBuildUI.util.helper.ModelHelper.getModel(me.getDataSourceType()[index], me.getDataSourceTypeName()[index]).then(function (model) {
                        var proxy = model.getProxy(),
                            storeconf = {
                                model: model.getName(),
                                proxy: {
                                    type: proxy.type,
                                    url: proxy.url,
                                    extraParams: {
                                        attrs: attrs.join(", ")
                                    }
                                },
                                autoDestroy: true,
                                autoLoad: false,
                                remoteSort: true,
                                sorters: [me.getEventStartDateAttribute()[index]],
                                pageSize: 0
                            };

                        // set filter
                        if (!Ext.isEmpty(me.getDataSourceFilter()) && !Ext.isEmpty(me.getTargetObject())) {
                            storeconf.advancedFilter = {
                                baseFilter: {
                                    ecql: CMDBuildUI.util.ecql.Resolver.resolve(me.getDataSourceFilter()[index], me.getTargetObject()[index])
                                }
                            };
                        }

                        // override proxy url
                        if (!Ext.isEmpty(me.getDataSourceUrl())) {
                            proxy = proxy.clone();
                            proxy.setUrl(me.getDataSourceUrl()[index]);
                            storeconf.proxy = proxy;
                        }

                        // override proxy extra params
                        if (me.getDataSourceExtraParams() && !Ext.Object.isEmpty(me.getDataSourceExtraParams()[index])) {
                            var params = storeconf.proxy.getExtraParams();
                            params = Ext.merge(params, me.getDataSourceExtraParams());
                            storeconf.proxy.setExtraParams(params);
                        }

                        // create store
                        me._currentstate[index].store = Ext.create("Ext.data.Store", storeconf);
                        deferred.resolve();
                    });
                } else {
                    deferred.resolve();
                }

                requests.push(deferred);
            });

            Ext.Promise.all(requests).then(function () {
                def.resolve();
            })

            return def.promise;

        },

        /**
         * Load classes store.
         *
         * @return {Ext.promise.Promise}
         */
        initCategories: function () {
            var me = this,
                def = new Ext.Deferred(),
                requests = [];

            Ext.Array.forEach(this.getDataSourceType(), function (item, index, allitems) {
                var deferred = new Ext.Deferred();
                if (me.getEventTypeAttribute() && me.getEventTypeLookup() && me.getEventTypeAttribute()[index] && me.getEventTypeLookup()[index]) {
                    var lt = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(me.getEventTypeLookup()[index]);
                    if (lt) {
                        lt.getLookupValues().then(function (values) {
                            me._currentstate[index].eventtypes = values;
                            deferred.resolve();
                        });
                    } else {
                        deferred.resolve();
                    }
                } else {
                    deferred.resolve();
                }

                requests.push(deferred);
            });

            Ext.Promise.all(requests).then(function () {
                def.resolve();
            })

            return def.promise;
        },

        /**
         *
         * @param {Object} eventClickInfo
         */
        defaultEventClickHandler: function (eventClickInfo) {
            var datasourcetype = this.getDataSourceType()[Ext.Array.indexOf(this.getDataSourceTypeName(), eventClickInfo.event.extendedProps.type)],
                config = {
                    viewModel: {
                        data: {
                            objectTypeName: eventClickInfo.event.extendedProps._objecttypename,
                            objectId: eventClickInfo.event.id
                        }
                    },
                    shownInPopup: true,
                    tabpaneltools: [],
                    padding: 10
                };
            if (datasourcetype === CMDBuildUI.util.helper.ModelHelper.objecttypes.klass) {
                config.xtype = 'classes-cards-card-view';
            } else if (datasourcetype === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                config.xtype = 'processes-instances-instance-view';
            }
            var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(eventClickInfo.event.extendedProps._objecttypename, datasourcetype),
                title = Ext.String.format("{0} &mdash; {1}", item.getTranslatedDescription(), eventClickInfo.event.title);
            CMDBuildUI.util.Utilities.openPopup(null, title, config);
        },

        /**
         * Resets the filter
         */
        onAdvancedFilterChange: function () {
            var start = this.calendar.view.activeStart,
                end = this.calendar.view.activeEnd;

            this._currentstate[0].start = null;
            this._currentstate[0].end = null;

            this.loadEvents(start, end);
        }
    }
});