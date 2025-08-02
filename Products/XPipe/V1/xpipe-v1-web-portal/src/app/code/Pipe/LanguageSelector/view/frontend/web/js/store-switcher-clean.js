define(['jquery', 'Magento_Customer/js/customer-data'], function ($, customerData) {
    'use strict';

    function clearCaches() {
        try {
            // Invalida tutte le sezioni customer-data (minicart, ecc.)
            customerData.invalidate(['*']);
            // Pulisce le chiavi di cache su localStorage (evita residui tra store)
            localStorage.removeItem('mage-cache-storage');
            localStorage.removeItem('mage-cache-timeout');
            localStorage.removeItem('mage-cache-sessid');
        } catch (e) { /* no-op */ }
    }

    // Intercetta il cambio su switcher lingua/store standard (core)
    $(document).on('change', '.switcher-language select, .switcher-store select, #lang-switcher', function () {
        // NON preveniamo il redirect core: lasciamo che la pagina si ricarichi
        clearCaches();
    });
});
