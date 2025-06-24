<?php

namespace Iam\Groups\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class GroupHelper
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