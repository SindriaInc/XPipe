<?php
namespace Support\TicketForm\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class TicketFormHelper
{
    public static function getSupportTicketFormTenant(): string
    {
        //return SystemEnvFacade::get('PIPELINES_DEDICATED_GITHUB_ORGANIZATION');
        return "Besteam";
    }
}
