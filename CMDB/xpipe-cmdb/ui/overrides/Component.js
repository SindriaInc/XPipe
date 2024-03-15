Ext.define('Overrides.Component', {
    override: 'Ext.Component',

    initComponent: function () {
        var me = this,
            localized = me.localized,
            value;

        if (Ext.isObject(localized) && !Ext.Object.isEmpty(localized)) {
            for (var prop in localized) {
                value = localized[prop];
                if (Ext.isString(value) && Ext.String.startsWith(value, "CMDBuildUI.locales.Locales")) {
                    /* jshint ignore:start */
                    try {
                        me[prop] = eval(value);
                    } catch (e) {
                        me[prop] = value;
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format("Label {0} not found", value),
                            CMDBuildUI.util.Logger.levels.error
                        )
                    }
                    /* jshint ignore:end */
                }
            }
        }
        me.callParent(arguments);
    },

    setUi: function (newUi) {
        var me = this;
        if (Ext.String.endsWith(me.xtype, 'tabpanel')) {
            try {
                me.setUI(newUi);
            } catch (error) {

            }
            return;
        }

        var oldUi = me.ui,
            currentNode = me.getEl() && me.getEl().dom;
        if (currentNode) {
            var childNodes = Ext.DomQuery.select('[id^=' + this.getId() + '][class*=' + oldUi + ']', currentNode);

            me.ui = newUi;
            var currentNodeClassList = Ext.String.splitWords(currentNode.getAttribute('class'));
            Ext.each(currentNodeClassList, function (cls) {
                if (cls.indexOf(oldUi) != -1) {
                    Ext.fly(currentNode).replaceCls(cls, cls.replace(oldUi, newUi));
                }
            });
            Ext.each(childNodes, function (node) {
                node = Ext.fly(node);
                var classList = Ext.String.splitWords(node.getAttribute('class'));
                Ext.each(classList, function (cls) {
                    if (cls.indexOf(oldUi) != -1) {
                        node.replaceCls(cls, cls.replace(oldUi, newUi));
                    }
                });
            });
        }
    }
});