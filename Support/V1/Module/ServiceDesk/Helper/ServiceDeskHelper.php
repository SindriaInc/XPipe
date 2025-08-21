<?php
namespace Support\ServiceDesk\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class ServiceDeskHelper
{
    public static function getSupportServiceDeskGithubOrganization(): string
    {
        //return SystemEnvFacade::get('PIPELINES_DEDICATED_GITHUB_ORGANIZATION');
        return "Besteam";
    }
}
