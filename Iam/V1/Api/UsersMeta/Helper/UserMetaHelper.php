<?php

namespace Iam\UsersMeta\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class UserMetaHelper
{
    public static function getIamCollectorBaseUrl()
    {
        return SystemEnvFacade::get('IAM_COLLECTOR_BASE_URL');
    }

    public static function getIamCollectorAdminUsername()
    {
        return SystemEnvFacade::get('IAM_COLLECTOR_ADMIN_USERNAME', 'carbon.user');
    }

    public static function getIamCollectorAdminPassword()
    {
        return SystemEnvFacade::get('IAM_COLLECTOR_ADMIN_PASSWORD', 'admin123');
    }

    public static function getIamUserAccessToken()
    {
        return SystemEnvFacade::get('IAM_USERS_ACCESS_TOKEN');
    }

    public static function getIamUserMetaAccessToken()
    {
        return SystemEnvFacade::get('IAM_USERS_META_ACCESS_TOKEN');
    }

    public static function getIamGroupsAccessToken()
    {
        return SystemEnvFacade::get('IAM_GROUPS_ACCESS_TOKEN');
    }

    public static function getIamPoliciesAccessToken()
    {
        return SystemEnvFacade::get('IAM_POLICIES_ACCESS_TOKEN');
    }



    public static function validatePayload(array $input): bool
    {
        $expectedKeys = ['username', 'jobTitle', 'seniority', 'location', 'workMode'];

        // Controlla che le chiavi principali siano esattamente quelle attese
        if (array_keys($input) !== $expectedKeys) {
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