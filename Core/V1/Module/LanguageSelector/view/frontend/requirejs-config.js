require.config({
    map: {
        '*': {
            'Magento_Store/switcher': 'Core_LanguageSelector/js/switcher'
        }
    }
});

console.log("✅ Core_LanguageSelector: requirejs override applied");
