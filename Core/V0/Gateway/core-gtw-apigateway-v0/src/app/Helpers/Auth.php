<?php

namespace App\Helpers;

class Auth
{
    public static function getAuthBaseUrl()
    {
        return env('XPIPE_CORE_AUTH_BASE_URL', 'https://auth.sindria.org');
    }

    public static function getAuthRealm()
    {
        return env('XPIPE_CORE_AUTH_REALM', 'sindria');
    }

    public static function getAuthClientId()
    {
        return env('XPIPE_CORE_AUTH_CLIENT_ID');
    }

    public static function getAuthClientSecret()
    {
        return env('XPIPE_CORE_AUTH_CLIENT_SECRET');
    }

    public static function getAuthAdminRealm()
    {
        return env('XPIPE_CORE_AUTH_ADMIN_REALM', 'master');
    }

    public static function getAuthAdminClientId()
    {
        return env('XPIPE_CORE_AUTH_ADMIN_CLIENT_ID');
    }

    public static function getAuthAdminClientSecret()
    {
        return env('XPIPE_CORE_AUTH_ADMIN_CLIENT_SECRET');
    }

    public static function getAuthAdminUsername()
    {
        return env('XPIPE_CORE_AUTH_ADMIN_USERNAME');
    }

    public static function getAuthAdminPassword()
    {
        return env('XPIPE_CORE_AUTH_ADMIN_PASSWORD');
    }
}