<?php
namespace Fnd\Notifications\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class NotificationsHelper
{
    public static function getFndCollectorBaseUrl()
    {
        return SystemEnvFacade::get('FND_COLLECTOR_BASE_URL');
    }

    public static function getFndCollectorAdminUsername()
    {
        return SystemEnvFacade::get('FND_COLLECTOR_ADMIN_USERNAME', 'carbon.user');
    }

    public static function getFndCollectorAdminPassword()
    {
        return SystemEnvFacade::get('FND_COLLECTOR_ADMIN_PASSWORD', 'admin123');
    }

    public static function getFndNotificationsAccessToken()
    {
        return SystemEnvFacade::get('FND_NOTIFICATIONS_ACCESS_TOKEN');
    }


}
