<?php

namespace Sindria\DashboardNotifications;

class Helper
{

    public static function getNotificationsUrl() : string
    {
        return env('APP_URL') . '/ajax/notifications';
    }

    public static function getSoundsUrl() : string
    {
        return plugin_dir_url( __FILE__ ) . 'static/sounds';
    }

}
