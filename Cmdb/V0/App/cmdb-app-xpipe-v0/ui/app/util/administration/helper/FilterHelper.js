Ext.define('CMDBuildUI.util.administration.helper.FilterHelper', {

    singleton: true,
    /**
     * Remove all unused filter keys and return cleaned object or string filter
     * @param {Object} filterObject 
     * @param {Boolean} [toString=undefined]
     * 
     * @returns {Object|String} return object if toString is undefined or null or false
     */
    removeEmptyFilterKeys: function (filterObject, toString) {
        var objectValues = Ext.Object.getValues(filterObject);
        var objectKeys = Ext.Object.getAllKeys(filterObject);
        Ext.Array.forEach(objectValues, function (value, index) {
            var isEmpty = value === null || typeof value === 'undefined';
            if (!isEmpty && typeof value === 'object') {
                if (typeof value.length != 'undefined') {
                    // is array
                    isEmpty = value.length === 0;
                } else {
                    // is object
                    isEmpty = Ext.Object.isEmpty(value);
                }
            }
            if (isEmpty) {
                delete filterObject[objectKeys[index]];
            }
        });
        if (toString) {
            return Ext.encode(filterObject);
        }
        return filterObject;

    },
    setRecordFilterFromPanel: function (popup, record, key, disableAutoclose, ignoreWarning) {
        var attrPanel = popup.down('administration-components-filterpanels-attributes-panel'),
            relPanel = popup.down('administration-components-filterpanels-relationfilters-panel'),
            funcPanel = popup.down('administration-components-filterpanels-functionfilters-panel'),
            queryPanel = popup.down('administration-components-filterpanels-fulltextfilter-panel'),
            attachmentspanel = popup.down("filters-attachments-panel");

        var value = {
            attribute: attrPanel ? attrPanel.getAttributesData() : null,
            relation: relPanel ? relPanel.getRelationsData() : null,
            functions: funcPanel ? funcPanel.getFunctionData() : null,
            query: queryPanel ? queryPanel.getQueryData() : null,
            attachment: attachmentspanel && attachmentspanel.getAttachmentsData() || null
        };
        var tempFilter = new CMDBuildUI.util.AdvancedFilter();
        tempFilter.applyAdvancedFilter(value);

        if (CMDBuildUI.util.helper.FiltersHelper.validityCheckFilter(tempFilter)) {
            value = CMDBuildUI.util.administration.helper.FilterHelper.removeEmptyFilterKeys(value, true, 'configuration');
            if (record && key) {
                record.set(key, value !== '{}' ? value : null);
            }
            if (!disableAutoclose) {
                popup.close();
            }
            if (!record && !key) {
                return value;
            }
        } else {
            if (!ignoreWarning) {
                Ext.asap(function () {
                    CMDBuildUI.util.Notifier.showWarningMessage(
                        Ext.String.format(
                            '<span data-testid="message-window-text">{0}</span>',
                            CMDBuildUI.locales.Locales.errors.invalidfilter
                        )
                    );
                });
            }
        }
    }
});