<?php
namespace Support\TicketForm\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class TicketFormHelper
{
    public static function getCoreConfigTenant(): string
    {
        return SystemEnvFacade::get('CORE_CONFIG_TENANT');
    }
}
