Ext.define('Overrides.Number', {
    override: 'Ext.Number',

    correctFloat: function (n) {
        // This is to correct the type of errors where 2 floats end with
        // a long string of decimals, eg 0.1 + 0.2. When they overflow in this
        // manner, they usually go to 15-16 decimals, so we cut it off at 14.
        if (n !== null && n !== undefined) {
            return parseFloat(n.toPrecision(14));
        }
        return n;
    }
});