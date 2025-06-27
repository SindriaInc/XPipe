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

    public static function validatePayload(array $input): bool
    {
        $expectedKeys = ['channel', 'severity', 'data'];
        $expectedDataKeys = ['entity', 'event', 'detail', 'url', 'isInternal'];

        // Controlla che le chiavi principali siano esattamente quelle attese
        if (array_keys($input) !== $expectedKeys) {
            return false;
        }

        // Controlla che 'data' sia un array
        if (!is_array($input['data'])) {
            return false;
        }

        // Controlla che le chiavi interne a 'data' siano esattamente quelle attese
        if (array_keys($input['data']) !== $expectedDataKeys) {
            return false;
        }

        return true;
    }


    public static function isJson(string $json) : bool
    {
        json_decode($json);
        return json_last_error() === JSON_ERROR_NONE;
    }


}
