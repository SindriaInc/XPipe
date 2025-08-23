<?php
namespace Support\TicketForm\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class TicketFormHelper
{
    public static function getSupportTicketFormTenant(): string
    {
        return SystemEnvFacade::get('CORE_CONFIG_TENANT');
    }
}
