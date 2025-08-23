<?php
namespace Support\ServiceDesk\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class ServiceDeskHelper
{
    public static function getSupportServiceDeskTenant(): string
    {
        return SystemEnvFacade::get('CORE_CONFIG_TENANT');
    }
}
