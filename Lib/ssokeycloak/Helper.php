<?php

namespace Sindria\SsoKeycloak;

class Helper
{
    public static function getAuthBaseUrl() : string
    {
        return env('XPIPE_WEB_AUTH_BASE_URL', 'https://auth.sindria.org');
    }

    public static function getAuthLegacyBaseUrl() : string
    {
        return env('XPIPE_WEB_AUTH_LEGACY_BASE_URL', 'https://auth.sindria.org/auth');
    }

    public static function getAuthRealm() : string
    {
        return env('XPIPE_WEB_AUTH_REALM', 'sindria');
    }

    public static function getAuthClientId() : string
    {
        return env('XPIPE_WEB_AUTH_CLIENT_ID');
    }

    public static function getAuthClientSecret() : string
    {
        return env('XPIPE_WEB_AUTH_CLIENT_SECRET');
    }

    public static function getAuthCallback() : string
    {
        return config('app.url') . '/' . session()->get('locale') . '/wp-login.php?action=callback';
    }


    public static function getCurrentTokens() : array
    {
        if (!session_id()) {
            session_start();
        }

        $tokens = [];

        if (! empty($_SESSION)) {
            foreach ($_SESSION as $entry) {
                if (preg_match('/(token_)(.*+)/', $entry)) {
                    $tokens[] = $entry;
                }
            }
        }

        return $tokens;
    }

    public static function getCurrentTokenData()
    {
        $tokens = self::getCurrentTokens();
        $currentData = [];

        if (! empty($tokens)) {
            $currentData = json_decode(end($tokens), true);
            return $currentData;
        }

        return $currentData;
    }

    public static function getCurrentAccessToken() : string
    {
        $currentToken = '';

        $data = self::getCurrentTokenData();

        if (! empty($data)) {
            $currentToken = $data['access_token'];
        }

        return $currentToken;
    }

    public static function getCurrentRefreshToken() : string
    {
        $currentToken = '';

        $data = self::getCurrentTokenData();

        if (! empty($data)) {
            $currentToken = $data['refresh_token'];
        }

        return $currentToken;
    }

    public static function getAutoprofileToggle() : bool
    {
        return env('SSO_KEYCLOAK_AUTOPROFILE_TOGGLE', false);
    }

    public static function getSsoButtonToggle() : bool
    {
        return env('SSO_KEYCLOAK_SSOBUTTON_TOGGLE', true);
    }

    public static function getSsoButtonIcon() : string
    {
        return  plugin_dir_url( __FILE__ ) . '/assets/images/sso/6.png';
    }
}
