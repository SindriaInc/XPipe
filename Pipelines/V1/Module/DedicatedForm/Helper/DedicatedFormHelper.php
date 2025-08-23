<?php
namespace Pipelines\DedicatedForm\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class DedicatedFormHelper
{
    public static function getCoreConfigTenant(): string
    {
        return SystemEnvFacade::get('CORE_CONFIG_TENANT');
    }

    public static function getSupportServiceDeskGitHubOrganization(): string
    {
        return SystemEnvFacade::get('SUPPORT_SERVICEDESK_GITHUB_ORGANIZATION');
    }

    public static function getSupportServiceDeskGitHubRepository(): string
    {
        return SystemEnvFacade::get('SUPPORT_SERVICEDESK_GITHUB_REPOSITORY');
    }

    public static function getSupportServiceDeskGitHubProjectNumber(): string
    {
        return SystemEnvFacade::get('SUPPORT_SERVICEDESK_GITHUB_PROJECT_NUMBER');
    }

    public static function getSupportServiceDeskGitHubProjectStatusId(): string
    {
        return SystemEnvFacade::get('SUPPORT_SERVICEDESK_GITHUB_PROJECT_STATUS_ID');
    }

    public static function getSupportServiceDeskGitHubProjectStatusOptionTriage(): string
    {
        return SystemEnvFacade::get('SUPPORT_SERVICEDESK_GITHUB_PROJECT_STATUS_OPTION_TRIAGE');
    }
}
