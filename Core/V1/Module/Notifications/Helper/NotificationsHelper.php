<?php

namespace Core\Notifications\Helper;

class NotificationsHelper
{
    public static function getCoreNotificationsAccessToken()
    {
        return SystemEnvHelper::get('CORE_NOTIFICATIONS_ACCESS_TOKEN');
    }

}
