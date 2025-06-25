<?php

namespace Pipe\PostLoginSetup\Helper;


use Core\SystemEnv\Facade\SystemEnvFacade;

class PostLoginSetupHelper
{

    public static function getPipelinesConfigmapVaultBaseUrl()
    {
        return SystemEnvFacade::get('PIPELINES_CONFIGMAP_VAULT_BASE_URL');
    }

    public static function getPipelinesConfigmapVaultAccessToken()
    {
        return SystemEnvFacade::get('PIPELINES_CONFIGMAP_VAULT_ACCESS_TOKEN');
    }

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


}
