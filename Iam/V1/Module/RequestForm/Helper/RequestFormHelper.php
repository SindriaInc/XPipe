<?php
namespace Iam\RequestForm\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class RequestFormHelper
{
    public static function getIamRequestFormTenant(): string
    {
        return SystemEnvFacade::get('CORE_CONFIG_TENANT');
    }
}
