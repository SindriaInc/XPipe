<?php
namespace Pipelines\TemplateCatalog\Helper;

class SystemEnvHelper
{
    public static function get($var, $default = null)
    {
        return getenv($var) ?: $default;
    }
}
