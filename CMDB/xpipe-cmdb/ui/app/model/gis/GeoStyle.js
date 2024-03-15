Ext.define('CMDBuildUI.model.gis.GeoStyle', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {

    },

    fields: [{
        name: 'externalGraphic',
        type: 'string',
        critical: true
    }, {
        name: 'fillColor',
        type: 'string',
        critical: true,
        defaultValue: '#000000'
    }, {
        name: 'fillOpacity',
        type: 'number',
        critical: true,
        defaultValue: '1'
    }, {
        name: 'fillOpacityCent',
        type: 'number',
        critical: true,
        calculate: function (data) {
            return parseFloat(data.fillOpacity * 100).toFixed(2);
        },
        serialize: function (value, record) {
            record.data.fillOpacity = parseFloat(value / 100).toFixed(2);
        }
    }, {
        name: 'pointRadius',
        type: 'number',
        critical: true,
        defaultValue: 3
    }, {
        name: 'strokeColor',
        type: 'string',
        critical: true,
        defaultValue: '#000000'
    }, {
        name: 'strokeDashstyle',
        type: 'string',
        critical: true,
        defaultValue: 'solid'
    }, {
        name: 'strokeOpacity',
        type: 'number',
        critical: true,
        defaultValue: 1
    }, {
        name: 'strokeOpacityCent',
        type: 'number',
        critical: true,
        calculate: function (data) {
            return parseFloat(data.fillOpacity * 100).toFixed(0);
        }
    }, {
        name: 'strokeWidth',
        type: 'number',
        critical: true,
        defaultValue: 1
    }],
    proxy: {
        type: 'memory'
    },

    /**
     * @returns {ol.style.Stroke}
     */
    getOlStroke: function (config) {
        config = config || {};
        return new ol.style.Stroke({
            color: config.color ? config.color : this.getStrokeColor(),
            width: config.width ? config.width : this.get('strokeWidth')
        });
    },

    /**
     * @returns {ol.style.Fill}
     */
    getOlFill: function () {
        return new ol.style.Fill({
            color: this.getFillColor()
        });
    },

    privates: {

        /**
         * @returns {Array}
         */
        getStrokeColor: function () {
            var c = this.hexToRgbA(this.get('strokeColor'));
            c.push(this.get('strokeOpacity'));

            return c;
        },

        /**
         * @returns {Array}
         */
        getFillColor: function () {
            var c = this.hexToRgbA(this.get('fillColor'));
            c.push(Ext.num(this.get('fillOpacity')));

            return c;
        },

        /**
         * This function changes the format of color so it can be readable from openlayer
         * @param {String} hex the input hexadecimal
         * @return an array rappresenting the hex color
         */
        hexToRgbA: function (hex) {
            if (hex == null) return [0, 0, 0];
            var c;
            if (/^#([A-Fa-f0-9]{3}){1,2}$/.test(hex)) {
                c = hex.substring(1).split('');
                if (c.length == 3) {
                    c = [c[0], c[0], c[1], c[1], c[2], c[2]];
                }
                c = '0x' + c.join('');
                return [(c >> 16) & 255, (c >> 8) & 255, c & 255]; // to set the transparency value add the alpha parameter to the returned ones
            }

            CMDBuildUI.util.Logger.log(
                "Color in bad hex sintax",
                CMDBuildUI.util.Logger.levels.error
            );
            return [0, 0, 0];
        }
    }
});