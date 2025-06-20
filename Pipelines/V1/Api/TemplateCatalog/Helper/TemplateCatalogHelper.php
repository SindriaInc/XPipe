<?php
namespace Pipelines\TemplateCatalog\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class TemplateCatalogHelper
{
    public static function getPipelinesCollectorBaseUrl()
    {
        return SystemEnvFacade::get('PIPELINES_COLLECTOR_BASE_URL');
    }

    public static function getPipelinesCollectorAdminUsername()
    {
        return SystemEnvFacade::get('PIPELINES_COLLECTOR_ADMIN_USERNAME', 'carbon.user');
    }

    public static function getPipelinesCollectorAdminPassword()
    {
        return SystemEnvFacade::get('PIPELINES_COLLECTOR_ADMIN_PASSWORD', 'admin123');
    }

    public static function getPipelinesTemplateCatalogAccessToken()
    {
        return SystemEnvFacade::get('PIPELINES_TEMPLATE_CATALOG_ACCESS_TOKEN');
    }


}
