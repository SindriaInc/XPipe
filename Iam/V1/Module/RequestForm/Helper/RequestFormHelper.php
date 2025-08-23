<?php
namespace Iam\RequestForm\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class RequestFormHelper
{
    public static function getCoreConfigTenant(): string
    {
        return SystemEnvFacade::get('CORE_CONFIG_TENANT');
    }
}
