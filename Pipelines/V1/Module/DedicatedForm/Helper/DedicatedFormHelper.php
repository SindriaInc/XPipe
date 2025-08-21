<?php
namespace Pipelines\DedicatedForm\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class DedicatedFormHelper
{
    public static function getSupportDedicatedFormTenant(): string
    {
        //return SystemEnvFacade::get('PIPELINES_DEDICATED_GITHUB_ORGANIZATION');
        return "Besteam";
    }
}
