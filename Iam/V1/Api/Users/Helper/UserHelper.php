<?php

namespace Iam\Users\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class UserHelper
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

    public static function getIamUsersAccessToken()
    {
        return SystemEnvFacade::get('IAM_USERS_ACCESS_TOKEN');
    }

    public static function getIamUsersMetaAccessToken()
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

    public static function getIamUsersIsBaseUrl()
    {
        return  SystemEnvFacade::get('IAM_USERS_IS_BASE_URL', 'https://auth.sindria.org');
    }

    public static function getIamUsersIsRealm()
    {
        return  SystemEnvFacade::get('IAM_USERS_IS_REALM', 'sindria');
    }

    public static function getIamUsersIsClientId()
    {
        return  SystemEnvFacade::get('IAM_USERS_IS_CLIENT_ID');
    }

    public static function getIamUsersIsClientSecret()
    {
        return  SystemEnvFacade::get('IAM_USERS_IS_CLIENT_SECRET');
    }

    public static function getIamUsersIsAdminRealm()
    {
        return  SystemEnvFacade::get('IAM_USERS_IS_ADMIN_REALM', 'master');
    }

    public static function getIamUsersIsAdminClientId()
    {
        return  SystemEnvFacade::get('IAM_USERS_IS_ADMIN_CLIENT_ID');
    }

    public static function getIamUsersIsAdminClientSecret()
    {
        return  SystemEnvFacade::get('IAM_USERS_IS_ADMIN_CLIENT_SECRET');
    }

    public static function getIamUsersIsAdminUsername()
    {
        return  SystemEnvFacade::get('IAM_USERS_IS_ADMIN_USERNAME');
    }

    public static function getIamUsersIsAdminPassword()
    {
        return  SystemEnvFacade::get('IAM_USERS_IS_ADMIN_PASSWORD');
    }


    public static function isAll($params) : bool
    {
        return count($params) === 0;
    }

    public static function isAllSearch($params) : bool
    {
        return isset($params['q']) && empty($params['q']) && count($params) === 1;
    }

    public static function isSearch($params) : bool
    {
        return isset($params['q']) && !empty($params['q']) && count($params) === 1;
    }

    public static function isPaginate($params) : bool
    {
        return isset($params['off']) && !empty($params['off']) !== null && isset($params['sze']) && !empty($params['sze']) !== null && count($params) === 2;
    }


    public static function selectFunction(array $params) : int
    {
        if (self::isAll($params)) {
            return 0;
        } else if (self::isAllSearch($params)) {
            return 0;
        } else if (self::isSearch($params)) {
            return 1;
        } else if (self::isPaginate($params)) {
            return 2;
        }

        return -1;
    }


}