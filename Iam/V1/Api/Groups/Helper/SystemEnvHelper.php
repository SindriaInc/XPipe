<?php
namespace Iam\Groups\Helper;

class SystemEnvHelper
{
    public static function get($var, $default = null)
    {
        return getenv($var) ?: $default;
    }
}
