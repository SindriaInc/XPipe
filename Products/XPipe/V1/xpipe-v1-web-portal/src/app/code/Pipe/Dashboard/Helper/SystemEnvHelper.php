<?php
namespace Pipe\Dashboard\Helper;

class SystemEnvHelper
{
    public static function get($var, $default = null)
    {
        return getenv($var) ?: $default;
    }
}
