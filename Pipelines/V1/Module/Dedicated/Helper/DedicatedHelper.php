<?php
namespace Pipelines\Dedicated\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class DedicatedHelper
{
    public static function getPipelinesDedicatedGithubTenant()
    {
        return SystemEnvFacade::get('PIPELINES_DEDICATED_GITHUB_TENANT');
    }
}
