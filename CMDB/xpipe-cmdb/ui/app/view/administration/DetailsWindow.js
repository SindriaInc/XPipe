(function () {
    var elementId = 'CMDBuildAdministrationDetailsWindow';

    Ext.define('CMDBuildUI.view.administration.DetailsWindow', {
        extend: 'CMDBuildUI.view.management.DetailsWindow',
        statics: {
            elementId: elementId
        },

        requires: [
            'CMDBuildUI.view.administration.DetailsWindowController',
            'CMDBuildUI.view.administration.DetailsWindowModel'
        ],
        alias: 'widget.administration-detailswindow',
        controller: 'administration-detailswindow',
        viewModel: {
            type: 'administration-detailswindow'
        },
        preventRefocus: true,
        bind: {
            title: '{title}'
        },

        id: elementId,
        autoEl: {
            'data-testid': 'cards-card-administration-detailsWindow'
        },
        ui: 'administration',
        layout: 'fit',
        autoShow: true,
        resizable: true,
        draggable: true,
        maximizable: true,
        monitorResize: true,
        defaultAlign: 'br-br',

        showAnimation: {
            type: 'popIn',
            duration: 250,
            easing: 'ease-out'
        },

        hideAnimation: {
            type: 'popOut',
            duration: 250,
            easing: 'ease-out'
        },
        listeners: {
            // bubbled events are not listened if declared 
            // in control properti within ViewControlle
            closed: 'onClosed',
            close: 'onClose',
            minimize: function (win, obj) {
                win.minimize();
            }
        },
        initComponent: function () {
            var viewportSize = Ext.getBody().getViewSize();
            Ext.apply(this, {
                height: viewportSize.height * 0.95,
                width: viewportSize.width * 0.75,
                tools: []
            });

            this.callParent(arguments);
        },

        /**
         * set position within body
         * @param {Numeric} x 
         * @param {Numeric} y 
         */
        afterSetPosition: function(x, y) {
            var newx, newy;
            var viewsize = Ext.getBody().getViewSize();
            if (x < 0) {
                newx = 1;
            } else if (x > (viewsize.width - this.getWidth())) {
                newx = viewsize.width - this.getWidth();
            }
            if (y < 0) {
                newy = 1;
            } else if (y > (viewsize.height - this.getHeight())) {
                newy = viewsize.height - this.getHeight();
            }

            if (newx !== undefined || newy !== undefined) {
                newx = newx || x;
                newy = newy || y;
                this.setPosition(newx, newy);
            }
        },

        /**
         * 
         * @param {Number} width 
         * @param {Number} height 
         * @param {Number} oldWidth 
         * @param {Number} oldHeight 
         */
        onResize: function(width, height, oldWidth, oldHeight) {            
            var viewsize = Ext.getBody().getViewSize();
            if (width > viewsize.width) {
                this.setWidth(viewsize.width);
            }
            if (height > viewsize.height) {
                this.setHeight(viewsize.height);
            }
        }
    });
})();