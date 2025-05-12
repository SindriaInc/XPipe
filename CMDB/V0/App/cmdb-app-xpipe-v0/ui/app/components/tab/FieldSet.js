Ext.define('CMDBuildUI.components.tab.FieldSet', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.formpaginationfieldset',

    ui: 'formpagination',

    /**
     * @override Ext.form.FieldSet
     * Override legend creation to move expander icon after the lable.
     */
    createLegendCt: function () {
        var me = this,
            items = [],
            legendCfg = {
                baseCls: me.baseCls + '-header',
                // use container layout so we don't get the auto layout innerCt/outerCt 
                layout: 'container',
                ui: me.ui,
                id: me.id + '-legend',
                autoEl: 'legend',
                ariaRole: null,
                items: items,
                ownerCt: me,
                shrinkWrap: true,
                ownerLayout: me.componentLayout
            },
            legend;
 
        // Checkbox 
        if (me.checkboxToggle) {
            items.push(me.createCheckboxCmp());
        }
 
        // Title 
        items.push(me.createTitleCmp());

        // Toggle button 
        if (!me.checkboxToggle && me.collapsible) {
            items.push(me.createToggleCmp());
        }

        legend = new Ext.container.Container(legendCfg);

        return legend;
    }
});