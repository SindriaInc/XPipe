
Ext.define('CMDBuildUI.view.administration.content.dashboards.card.chart.PreviewContainer', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-content-dashboards-card-chart-previewcontainer',
    style: 'border: 1px solid #c3c3c3;',
    viewModel: {
        stores: {
            classes: {
                type: 'chained',
                source: 'classes.Classes',
                autoDestroy: true
            }
        }
    }


});
