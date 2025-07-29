<?php
namespace Pipelines\PipeManager\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class PipeManagerHelper
{
    public static function getPipelinesPipeManagerGithubOrganization()
    {
        return SystemEnvFacade::get('PIPELINE_PIPEMANAGER_GITHUB_ORGANIZATION');
    }
}
