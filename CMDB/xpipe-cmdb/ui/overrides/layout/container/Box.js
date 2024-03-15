Ext.define('Overrides.layout.container.Box', {
    override: 'Ext.layout.container.Box',
    roundFlex: function(width) {
        return Math.round(width);
    }
});