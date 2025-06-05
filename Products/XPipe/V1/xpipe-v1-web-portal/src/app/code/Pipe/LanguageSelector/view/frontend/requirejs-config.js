require.config({
    map: {
        '*': {
            'Magento_Store/switcher': 'Pipe_LanguageSelector/js/switcher'
        }
    }
});

console.log("✅ Pipe_LanguageSelector: requirejs override applied");
