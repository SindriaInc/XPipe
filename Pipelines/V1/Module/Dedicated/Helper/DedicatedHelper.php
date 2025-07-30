<?php
namespace Pipelines\Dedicated\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class DedicatedHelper
{
    public static function getPipelinesDedicatedGithubOrganization()
    {
        //return SystemEnvFacade::get('PIPELINES_DEDICATED_GITHUB_ORGANIZATION');
        return "XPipePipelines";
    }
}
