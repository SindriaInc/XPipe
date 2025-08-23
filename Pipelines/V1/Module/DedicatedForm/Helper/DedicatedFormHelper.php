<?php
namespace Pipelines\DedicatedForm\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class DedicatedFormHelper
{
    public static function getSupportDedicatedFormTenant(): string
    {
        return SystemEnvFacade::get('CORE_CONFIG_TENANT');
    }
}
