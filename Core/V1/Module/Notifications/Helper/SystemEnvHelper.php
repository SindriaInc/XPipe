<?php
namespace Core\Notifications\Helper;

class SystemEnvHelper
{
    public static function get($var, $default = null)
    {
        return getenv($var) ?: $default;
    }
}
