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
}
