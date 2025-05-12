<?php

namespace App\Helpers;

use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Str;

class AuthHelper
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
}
