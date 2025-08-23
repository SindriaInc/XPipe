<?php
namespace Support\ServiceDesk\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class ServiceDeskHelper
{
    public static function getCoreConfigTenant(): string
    {
        return SystemEnvFacade::get('CORE_CONFIG_TENANT');
    }
}
