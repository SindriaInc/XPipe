<?php
namespace Pipelines\TemplateCatalog\Helper;

class TemplateCatalogHelper
{
    public static function getAdminUsername()
    {
        return SystemEnvHelper::get('API_COLLECTOR_ADMIN_USERNAME', 'carbon.user');
    }

    public static function getAdminPassword()
    {
        return SystemEnvHelper::get('API_COLLECTOR_ADMIN_PASSWORD', 'admin123');
    }

    public static function getPipelinesTemplateCatalogAccessToken()
    {
        return SystemEnvHelper::get('PIPELINES_TEMPLATE_CATALOG_ACCESS_TOKEN', '1234');
    }

    public static function getApiCollectorBaseUrl()
    {
        return SystemEnvHelper::get('API_COLLECTOR_BASE_URL', 'http://172.16.10.101');
    }
}
