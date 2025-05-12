<?php

if (!function_exists('env')) {
    /**
     * Get env variable with default fallback
     *
     * @param $key string
     * @param $default string
     */
    function env(string $key, string $default = '')
    {
        $env = getenv($key);

        if (! $env) {
            $env = $default;
        }

        return $env;
    }
}

function cookiebanner_cookie_banner() {

    echo '<script type="text/javascript" src="//cdn.iubenda.com/cs/gpp/stub.js"></script>';
    echo '<script type="text/javascript" src="//cdn.iubenda.com/cs/iubenda_cs.js" charset="UTF-8" async></script>';

    ?>

    <script type="text/javascript">
        var _iub = _iub || [];
        _iub.csConfiguration = {
            "askConsentAtCookiePolicyUpdate": true,
            "countryDetection": true,
            "enableLgpd": true,
            "enableUspr": true,
            "floatingPreferencesButtonDisplay": "bottom-right",
            "lang": "en",
            "lgpdAppliesGlobally": false,
            "perPurposeConsent": true,
            "siteId": 3131974,
            "whitelabel": false,
            "cookiePolicyId": 98271198,
            "banner": {
                "acceptButtonDisplay": true,
                "closeButtonDisplay": false,
                "customizeButtonDisplay": true,
                "explicitWithdrawal": true,
                "listPurposes": true,
                "position": "bottom",
                "rejectButtonDisplay": true,
                "showPurposesToggles": true
            }
        };
    </script>

    <?php
}
