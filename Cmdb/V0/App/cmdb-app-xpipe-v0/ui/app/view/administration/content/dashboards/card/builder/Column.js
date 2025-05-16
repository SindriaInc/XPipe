Ext.define('CMDBuildUI.view.administration.content.dashboards.card.builder.Column', {
    extend: 'Ext.view.View',
    requires: [
        'CMDBuildUI.view.administration.content.dashboards.card.builder.ColumnController',
        'CMDBuildUI.view.administration.content.dashboards.card.builder.ColumnModel'
    ],
    alias: 'widget.administration-content-dashboards-card-builder-column',
    controller: 'administration-content-dashboards-card-builder-column',
    viewModel: {
        type: 'administration-content-dashboards-card-builder-column'
    },
    config: {
        colIndex: 0,
        rowIndex: 0,
        isAllFreeItems: false
    },
    scrollable: false,    
    cls: 'cmdbuild-fieldsmanagements-column',
    viewConfig: {
        navigationModel: {}
    },

    prepareData: function (data, index, record) {
        if (this.getIsAllFreeItems()) {
            return data;
        }
        try {
            var chart = this.lookupViewModel().get('theDashboard.charts').findRecord('_id', data);
            chart.set('descriptionWithName', chart.get('description'));
            return chart.getData();

        } catch (error) {
            CMDBuildUI.util.Logger.log("something wrong in charts list", CMDBuildUI.util.Logger.levels.debug);
        }
    },
    itemSelector: 'div.attribute-source',
    overItemCls: 'attribute-over',
    selectedItemClass: 'attribute-selected',
    singleSelect: true,
    layout: 'fit',
    initComponent: function () {
        this.tpl = new Ext.XTemplate('<tpl for=".">',
            '<tpl if="!this.isAllFreeItems(values)">',
            '<div id="{[this.getToolId(values._id,\'container\')]}" style="width:100%; min-width:150px; overflow: hidden; text-overflow: ellispis; white-space: nowrap" class="attribute-source x-unselectable" data-testid="administration-content-dashboards-card-builder-chart-column">',            
            '<table id="{[this.getToolId(values._id,\'table\')]}" style="width:100%;  >',
            '<tbody>',
            '<tr >',
            '<td class="attribute-name" style="text-overflow: ellispis; white-space: nowrap"><div class="draggable" style="position:relative; display:block">',
            '<span style="margin-left: 5px;">{[this.getIcon(values)]} {[this.getChartType(values)]}</span>',

            '<tpl if="this.isEditMode()">',
            '<span class="draggable-tool" style="position:absolute;right: 85px; top: 5px"><div id="{[this.getToolId(values._id,\'edit\')]}" class="x-tool administration-tool" aria-disabled="false" data-qtip="{[this.getQtip(\'edit\')]}"><div data-event="edit" data-ref="toolEl" class="x-tool-tool-el x-tool-tool-el x-fa fa-pencil"></div></div></span>',
            '<span class="draggable-tool" style="position:absolute;right: 65px; top: 5px"><div id="{[this.getToolId(values._id,\'open\')]}" class="x-tool administration-tool" aria-disabled="false" data-qtip="{[this.getQtip(\'open\')]}"><div data-event="open" data-ref="toolEl" class="x-tool-tool-el x-tool-tool-el x-fa fa-external-link "></div></div></span>',
            '<tpl if="!this.isChartDisabled(values)">',
            '<span class="draggable-tool" style="position:absolute;right: 45px; top: 5px"><div id="{[this.getToolId(values._id,\'disable\')]}" class="x-tool administration-tool" aria-disabled="false" data-qtip="{[this.getQtip(\'disable\')]}"><div data-event="disable" data-ref="toolEl" class="x-tool-tool-el x-tool-tool-el x-fa fa-ban "></div></div></span>',
            '<tpl else>',
            '<span class="draggable-tool" style="position:absolute;right: 45px; top: 5px"><div id="{[this.getToolId(values._id,\'enable\')]}" class="x-tool administration-tool" aria-disabled="false" data-qtip="{[this.getQtip(\'enable\')]}"><div data-event="enable" data-ref="toolEl" class="x-tool-tool-el x-tool-tool-el x-fa fa-check-circle-o"></div></div></span>',
            '</tpl>',
            '<span class="draggable-tool" style="position:absolute;right: 25px; top: 5px"><div id="{[this.getToolId(values._id,\'clone\')]}" class="x-tool administration-tool" aria-disabled="false" data-qtip="{[this.getQtip(\'clone\')]}"><div data-event="clone" data-ref="toolEl" class="x-tool-tool-el x-tool-tool-el x-fa fa-clone  "></div></div></span>',
            '<span class="draggable-tool" style="position:absolute;right: 5px; top: 5px"><div id="{[this.getToolId(values._id,\'delete\')]}" class="x-tool administration-tool" aria-disabled="false" data-qtip="{[this.getQtip(\'delete\')]}"><div data-event="delete" data-ref="toolEl" class="x-tool-tool-el x-tool-tool-el x-fa fa-trash "></div></div></span>',
            '<tpl else>',
            '<span class="draggable-tool" style="position:absolute;right: 5px; top: 5px"><div id="{[this.getToolId(values._id,\'open\')]}" class="x-tool administration-tool" aria-disabled="false" data-qtip="{[this.getQtip(\'open\')]}"><div data-event="open" data-ref="toolEl" class="x-tool-tool-el x-tool-tool-el x-fa fa-external-link "></div></div></span>',
            '</tpl>',
            '</div></td>',
            '</tr>',
            '<tr>',            
            '</tr>',
            '</tbody>',
            '</table>',
            '</div>',

            '<tpl else>',
            '<div class="attribute-source x-unselectable" data-testid="administration-content-dashboards-card-builder-chart-column">',
            '<table style="display:flex;flex:1; overflow: hidden; text-overflow:ellipsis">',
            '<tbody>',
            '<tr>',
            '<td class="attribute-name" style=" flex-direction: row; flex: 1;overflow: hidden; text-overflow: ellispis; white-space: nowrap">',
            '<span style="float: left; flex-direction: row; flex: 1;overflow: hidden; text-overflow: ellispis; white-space: nowrap">{[this.getIcon(values)]} {[this.getDescription(values)]}</span>',
            '</td>',
            '</tr>',
            '</tbody></table>',
            '</div>',
            '</tpl>',
            '<div style="display:block; position:relative" id="ph_{[this.getChart(values)]}" class="chartplaceholder" data-chart="{[this.getChart(values)]}"></div>',
            '</tpl>', {
                scope: this,
                isChartDisabled: function (value) {
                    return !value.active;
                },
                isEditMode: function () {
                    var vm = this.scope.lookupViewModel();
                    return !vm.get('actions.view');
                },
                getQtip: function (item) {
                    switch (item) {
                        case 'edit':
                            return CMDBuildUI.locales.Locales.administration.common.actions.edit;
                        case 'open':
                            return CMDBuildUI.locales.Locales.administration.common.actions.open;
                        case 'delete':
                            return CMDBuildUI.locales.Locales.administration.common.actions.delete;
                        case 'enable':
                            return CMDBuildUI.locales.Locales.administration.common.actions.enable;
                        case 'disable':
                            return CMDBuildUI.locales.Locales.administration.common.actions.disable;
                        case 'clone':
                            return CMDBuildUI.locales.Locales.administration.common.actions.clone;

                        default:
                            break;
                    }
                },
                getToolId: function (recordId, toolType) {
                    return Ext.String.format('{0}_{1}_tool', this.scope.id, toolType);
                },
                isAllFreeItems: function (value) {
                    return this.scope.getIsAllFreeItems();
                },
                getChartType: function (value) {
                    var chartDefinition = CMDBuildUI.model.dashboards.Chart.getChartTypes(value.type);
                    return chartDefinition.label;
                },
                getDescription: function (value) {
                    if (typeof value.descriptionWithName !== 'undefined') {
                        return value.descriptionWithName || '&nbsp;';
                    }
                    return value;
                },
                getIcon: function (value) {
                    var chartDefinition = CMDBuildUI.model.dashboards.Chart.getChartTypes(value.type || value.value);
                    return Ext.String.format('<span class="{0}"></span>', chartDefinition.iconCls);
                },
                getChart: function (value) {
                    return value._id;
                }
            });
        this.callParent(arguments);
    },
    listeners: {
        itemclick: function (view, record, item, index, e, eOpts) {
            try {
                var clickedEl = Ext.get(e.target);
                this.fireEventArgs(Ext.String.format('item{0}click', clickedEl.dom.dataset.event), [record, e, eOpts]);
                e.stopPropagation();
                return false;
            } catch (event) {
                e.stopPropagation();
                return false;
                // do nothing
                // it doesn't have an event declared
            }
        },
        itemdblclick: function (view, record, item, index, e, eOpts) {
            if (view.lookupViewModel().get('actions.view')) {
                this.fireEventArgs('itemopenclick', [record, e, eOpts, true]);
            } else {
                this.fireEventArgs('itemeditclick', [record, e, eOpts]);
            }
            return false;
        },

        /*
         * Here is where we "activate" the DataView.
         * We have decided that each node with the class "attribute-source" encapsulates a single draggable
         * object.
         *
         * So we inject code into the DragZone which, when passed a mousedown event, interrogates
         * the event to see if it was within an element with the class "attribute-source". If so, we
         * return non-null drag data.
         *
         * Returning non-null drag data indicates that the mousedown event has begun a dragging process.
         * The data must contain a property called "ddel" which is a DOM element which provides an image
         * of the data being dragged. The actual node clicked on is not dragged, a proxy element is dragged.
         * We can insert any other data into the data object, and this will be used by a cooperating DropZone
         * to perform the drop operation.
         */

        render: function (view) {
            var me = this;
            view.dragZone = Ext.create('Ext.dd.DragZone', view.getEl(), {
                onBeforeDrag: function (data, e) {
                    if (!e.target.dataset.event) {
                        var charts = Ext.ComponentQuery.query('dashboards-chart');
                        Ext.Array.forEach(charts, function (chart) {
                            try {
                                chart.collapse();
                            } catch (e) {

                            }
                        });
                    } else {
                        return false;
                    }

                },

                //      On receipt of a mousedown event, see if it is within a draggable element.
                //      Return a drag data object if so. The data object can contain arbitrary application
                //      data, but it should also contain a DOM element in the ddel property to provide
                //      a proxy to drag.
                getDragData: function (e) {
                    var sourceEl = e.getTarget(view.itemSelector, 10),
                        d;
                    if (sourceEl && !view.lookupViewModel().get('actions.view')) {
                        d = sourceEl.cloneNode(true);
                        d.id = Ext.id();

                        return (view.dragData = {
                            sourceEl: sourceEl,
                            sourceView: view,
                            repairXY: Ext.fly(sourceEl).getXY(),
                            ddel: d,
                            chartData: view.getRecord(sourceEl).data
                        });
                    }
                },

                //      Provide coordinates for the proxy to slide back to on failed drag.
                //      This is the original XY coordinates of the draggable element.
                getRepairXY: function () {
                    return this.dragData.repairXY;
                }
            });


            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            function getDataFromTarget(target) {
                var fieldset = view.up('view-administration-content-dashboards-card');
                var vm = fieldset.lookupViewModel();
                var gridStore = vm.get('rows').getRange();
                if (gridStore) {
                    return gridStore;
                }
                return false;
            }

            function getAllChartStore() {
                var fieldset = view.up('view-administration-content-dashboards-card');
                var vm = fieldset.lookupViewModel();
                var allChartsStore = vm.get('chartsStore');
                return allChartsStore;
            }

            function allowChart(name) {
                var chartsStore = getAllChartStore();
                var record = chartsStore.findRecord('value', name);
                if (!record) {
                    record = view.lookupViewModel().get('theDashboard.charts').findRecord('_id', name);
                }
                return record;
            }
            view.dropZone = Ext.create('Ext.dd.DropZone', view.el, {

                // If the mouse is over a target node, return that node. This is
                // provided as the "target" parameter in all "onNodeXXXX" node event handling functions
                getTargetFromEvent: function (e) {
                    return e.getTarget('.cmdbuild-fieldsmanagements-column');
                },

                // // On entry into a target node, highlight that node.
                onNodeEnter: function (target, dd, e, data) {
                    Ext.fly(target).addCls('column-target-hover');
                },

                // On exit from a target node, unhighlight that node.
                onNodeOut: function (target, dd, e, data) {
                    Ext.fly(target).removeCls('column-target-hover');
                },

                // While over a target node, return the default drop allowed class which
                // places a "tick" icon into the drag proxy.
                onNodeOver: function (target, dd, e, data) {
                    var name = data.chartData.value,
                        proto = Ext.dd.DropZone.prototype,
                        targetView = Ext.getCmp(target.id);
                    if (data.sourceView.isAllFreeItems && targetView.isAllFreeItems) {
                        return proto.dropNotAllowed;
                    }
                    return typeof data.chartData === 'string' || allowChart(name) ? proto.dropAllowed : proto.dropNotAllowed;
                },

                // On node drop, we can interrogate the target node to find the underlying
                // application object that is the real target of the dragged data.
                // In this case, it is a Record in the GridPanel's Store.
                // We can use the data set up by the DragZone's getDragData method to read
                // any data we decided to attach.
                onNodeDrop: function (target, dd, e, data) {
                    var name = data.chartData.value || data.chartData;

                    var chart = allowChart(name);
                    if (chart) {
                        var sourceStore = data.sourceView.getStore();
                        var targetView = Ext.getCmp(target.id);
                        var groupData = getDataFromTarget();
                        if (data.sourceView.getIsAllFreeItems() && targetView.getIsAllFreeItems()) {
                            return false;
                        }

                        var sourceIndex;
                        if (!data.sourceView.getIsAllFreeItems()) {
                            sourceIndex = sourceStore.findExact('chart', data.chartData);
                        } else {
                            sourceIndex = sourceStore.findExact('value', chart.get('value'));
                        }
                        /**
                         * logic for sort
                         */
                        var orderIndex;
                        if (!targetView.isAllFreeItems) {
                            var releaseY = e.clientY - view.getY();
                            orderIndex = view.getNodes().length;
                            try {
                                Ext.Array.forEach(view.getNodes(), function (node, index) {
                                    if (releaseY < (node.offsetTop + (node.clientHeight / 2))) {
                                        throw index;
                                    } else if (
                                        releaseY > (node.offsetTop + (node.clientHeight / 2)) &&
                                        releaseY < (node.offsetTop + (node.clientHeight))
                                    ) {
                                        throw index + 1;
                                    }
                                });
                            } catch (index) {
                                if (data.sourceView === targetView && sourceIndex < index) {
                                    orderIndex = index - 1;
                                } else {
                                    orderIndex = index;
                                }
                            }
                        } else {
                            orderIndex = chart.get('index');
                        }

                        /*****/
                        if (!data.sourceView.getIsAllFreeItems()) {
                            // remove chart from column store
                            sourceStore.removeAt(sourceIndex);
                        }

                        var sRowIndex = data.sourceView.getRowIndex();
                        var sColIndex = data.sourceView.getColIndex();
                        var sRows = groupData;
                        var sColumns = sRows[sRowIndex].columns || sRows[sRowIndex].get('columns');
                        // chart.set('descriptionWithName', chart.getDescriptionWithName());

                        var sCharts = sColumns[sColIndex].charts;

                        if (chart.get('_id')) {
                            var sChart = Ext.Array.findBy(sCharts, function (_chart) {
                                return _chart === chart.get('_id') || _chart.chart === chart.get('name');
                            });
                            // remove chart from source
                            Ext.Array.remove(sCharts, sChart);
                        }
                        if (data.sourceView.getIsAllFreeItems()) {
                            // is new chart

                            // generate new id

                            var newChart = CMDBuildUI.model.dashboards.Chart.create({
                                _id: CMDBuildUI.util.Utilities.generateUUID(),
                                type: chart.get('value'),
                                dataSourceType: chart.get('value') === 'text' ? CMDBuildUI.model.dashboards.Chart.dataSourceTypes.text : CMDBuildUI.model.dashboards.Chart.dataSourceTypes.funktion
                            });
                            // source store index

                            var columns = groupData[view.getRowIndex()].columns || groupData[view.getRowIndex()].get('columns');
                            columns[view.getColIndex()].charts.push(newChart.get('_id'));
                            view.up('view-administration-content-dashboards-card')
                                .getViewModel()
                                .get('theDashboard')
                                .charts()
                                .add(newChart);

                            me.fireEventArgs('itemcreated', [newChart]);
                            view.lookupViewModel().get('rows').setData(sRows);
                            return true;
                        } else {
                            // is column of form
                            if (chart) {
                                var targetComponent = Ext.getCmp(target.id);
                                targetComponent.getStore().insert(orderIndex, chart.get('id'));
                                // find the column where to push the chart
                                var tRowIndex = targetView.getRowIndex();
                                var tColIndex = targetView.getColIndex();
                                var tColumns = sRows[tRowIndex].columns || sRows[tRowIndex].get('columns');
                                tColumns[tColIndex].charts = Ext.Array.insert(tColumns[tColIndex].charts, orderIndex, [chart.get('_id')]);
                                Ext.suspendLayouts();
                                view.lookupViewModel().get('rows').setData(sRows);
                                Ext.resumeLayouts();
                            }



                            CMDBuildUI.util.Logger.log('Dropped chart ' + name +
                                ' on column ', CMDBuildUI.util.Logger.levels.debug, 'Drop gesture');

                            return true;
                        }
                    }
                }
            });
            view.up('fieldset').updateLayout();

        }
    }
});