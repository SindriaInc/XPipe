/**
 * @file CMDBuildUI.util.ecql.Resolver
 * @module CMDBuildUI.util.ecql.Resolver
 * @author Tecnoteca srl 
 * @access public
 */
Ext.define("CMDBuildUI.util.ecql.Resolver", {
    singleton: true,

    /**
     * Resolve eCQL
     * 
     * @param {Object} filter eCQL configuration.
     * @param {String} filter.id eCQL id.
     * @param {Object} filter.bindings eCQL bindings definition.
     * @param {String[]} filter.bindings.client List of binding client attributes.
     * @param {String[]} filter.bindings.server List of binding server attributes.
     * @param {Ext.data.Model} target The target model instance.
     * 
     * @returns {Obejct} 
     * The eCQL filter.
     * {
     *      id: "ecqlId",
     *      context: "{client:{some:value},server:{wath:ever}}"
     * }
     * 
     */
    resolve: function (filter, target) {
        if (filter) {
            var context = {
                client: this.resolveClientVariables(filter.bindings.client, target),
                server: this.resolveServerVariables(filter.bindings.server, target)
            };

            return {
                id: filter.id,
                context: Ext.JSON.encode(context)
            };
        }
    },

    /**
     * Resolve client variables.
     * 
     * @param {String[]} variables List of binding client attributes.
     * @param {Ext.data.Model} target The target model instance.
     * 
     * @returns {Object}
     * 
     */
    resolveClientVariables: function (variables, target) {
        if (variables && variables.length) {
            var data = target ? target.getData() : {};
            return this.getValuesFromData(data, variables);
        }
        return {};
    },

    /**
     * Resolve server variables.
     * 
     * @param {String[]} variables List of binding server attributes.
     * @param {Ext.data.Model} target The target model instance.
     * 
     * @returns {Object}
     * 
     */
    resolveServerVariables: function (variables, target) {
        // TODO: get original data
        if (variables && variables.length) {
            var data = target ? target.getData() : {};
            return this.getValuesFromData(data, variables);
        }
        return {};
    },

    /**
     * Get bindings.
     * 
     * @param {Object} filter eCQL configuration.
     * @param {Object} filter.bindings eCQL bindings definition.
     * @param {String[]} filter.bindings.client List of binding client attributes.
     * @param {String} linkname The link name to use to build the binds.
     * @returns {Object} An object containing all keys
     * 
     */
    getViewModelBindings: function (filter, linkname) {
        var bindings = {};
        for (var i = 0; i < filter.bindings.client.length; i++) {
            var attr = filter.bindings.client[i];
            var sattr = attr.split(".");
            if (sattr.length === 1) {
                bindings[sattr[0]] = Ext.String.format("{{0}.{1}}", linkname, CMDBuildUI.util.Utilities.stringRemoveSpecialCharacters(sattr[0]));
            } else if (sattr.length === 2 && (sattr[1] === "Id" || sattr[1] === "Code" || sattr[1] === "Description")) {
                bindings[sattr[0]] = Ext.String.format("{{0}.{1}}", linkname, CMDBuildUI.util.Utilities.stringRemoveSpecialCharacters(sattr[0]));
            }
        }
        return bindings;
    },

    privates: {
        /**
         * @private
         * @param {Object} data
         * @param {String[]} keys
         * @return {Object}
         */
        getValuesFromData: function (data, keys) {
            var values = {};

            keys.forEach(function (attr) {
                var sattr = attr.split("."),
                    field, val;
                if (sattr.length === 1) {
                    field = CMDBuildUI.util.Utilities.stringRemoveSpecialCharacters(sattr[0]);
                    if (field === "Id") {
                        field = "_id";
                    }
                    val = data[field];
                } else if (sattr.length === 2) {
                    var value = data[sattr[0]],
                        isLookupArray = Ext.isArray(value);
                    if (!isLookupArray) {
                        switch (sattr[1]) {
                            case "Id":
                                field = CMDBuildUI.util.Utilities.stringRemoveSpecialCharacters(sattr[0]);
                                break;
                            case 'Description':
                                field = Ext.String.format('_{0}_description', sattr[0]);
                                break;
                            case 'Code':
                                field = Ext.String.format('_{0}_code', sattr[0]);
                                break;
                        }

                        //get the value
                        if (field) {
                            val = data[field] !== 0 ? data[field] : null;
                        }
                    } else if (!Ext.isEmpty(value)) {
                        val = [];
                        Ext.Array.forEach(data["_" + sattr[0] + "_details"], function (item, index, allitems) {
                            var prop = sattr[1] === "Id" ? "_id" : sattr[1].toLowerCase(),
                                elem = item[prop];
                            if (elem) {
                                val.push(elem);
                            }
                        });
                    }
                }
                values[attr] = Ext.isEmpty(val) && !Ext.isArray(val) ? null : val;
            });

            return values;
        }
    }
});