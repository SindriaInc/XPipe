<?php
namespace Iam\RequestForm\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class RequestFormHelper
{
    public static function getIamRequestFormTenant(): string
    {
        //return SystemEnvFacade::get('PIPELINES_DEDICATED_GITHUB_ORGANIZATION');
        return "Besteam";
    }
}
