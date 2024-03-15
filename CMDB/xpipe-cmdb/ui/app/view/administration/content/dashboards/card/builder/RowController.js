Ext.define('CMDBuildUI.view.administration.content.dashboards.card.builder.RowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dashboards-card-builder-row',

    control: {
        '#': {
            afterrender: 'onAfterRender'
        }
    },

    onAfterRender: function (view) {        
        var me = this;
        var vm = this.getViewModel();
        var row = view.getWidgetRecord();

        vm.set('row', row);
        var columns = row.get('columns');
        var rowIndex = row.store.findExact('id', row.get('id'));
        // var group = view.up('administration-components-attributes-fieldsmanagement-group-group').getGroup().get('_id');
        vm.set('columnsLength', columns.length);

        Ext.Array.forEach(columns, function (col, colIndex) {
            view.add(view.addColumn(col, colIndex, rowIndex));
        });        
    },



    addToolbar: function (group, row) {
        return {
            xtype: 'container',
            layout: {
                type: 'vbox'
            },
            items: []
        };
    },

    getFirstSizeOption: function (size) {
        var columnsWidthBtn = this.getView().down('#columnsWidthBtn');
        var values;
        columnsWidthBtn.getMenu().items.each(function (item) {
            // if values is undefined, search first item visible
            if (!values && item.columnsLength === size) {
                values = item.value;
            }
        });
        return values;
    },


    columnsBtnItemClick: function (menuItem) {
        var me = this;
        var vm = me.getViewModel();
        var view = me.getView();
        var button = menuItem.up('button');
        var toolbar = button.up('components-administration-toolbars-formtoolbar');
        var row = menuItem.up('administration-content-dashboards-card-builder-row');
        var rowColumns = row.items.length - 1;
        var buttonValue = menuItem.columns;
        var groupId = view.up('view-administration-content-dashboards-card').getGroup().get('_id');
        var i;
                
        if (rowColumns < buttonValue) {
            for (i = 0; i < buttonValue - rowColumns; i++) {
                row.insert(rowColumns + i, me.addColumn(groupId));
            }
            button.setText(menuItem.columns);            
        } else if (rowColumns > buttonValue) {
            var toRemove = [];
            var j = 0;
            for (i = buttonValue; i < rowColumns; i++) {
                var currentColumn = row.items.items[j];

                if (!currentColumn.getStore().getRange().length) {                   
                    // remove column
                    toRemove.push(row.items.items[j]);
                }
                j++;
            }
            Ext.Array.forEach(toRemove, function (item) {
                row.remove(item);

            });

            button.setText(row.items.length - 1);
            vm.set('columnsLength', parseInt(button.getText()));            
        } else {

            // do nothing
        }

        me.setColumnsFlex(row, me.getFirstSizeOption(parseInt(button.getText())));
    }



});
