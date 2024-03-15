Ext.define('CMDBuildUI.view.events.event.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.events-event-view',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * 
     * @param {*} view 
     * @param {*} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        vm.bind('{events-event-view.theEvent}', function (theEvent) {
            if (theEvent) {
                view.generateForm.call(view);
            }
        }, view);
    },

    privates: {
        /**
         * Get resource base path for routing.
         * @return {String}
         */
        getBasePath: function () {
            var view = this.getView();
            var theEvent = view.getTheEvent();

            return 'events/' + theEvent.getId();
        }
    }
});
