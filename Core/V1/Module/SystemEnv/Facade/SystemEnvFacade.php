<?php
namespace Core\SystemEnv\Facade;

use Core\SystemEnv\Helper\SystemEnvHelper;

class SystemEnvFacade
{
    private static ?SystemEnvHelper $helper = null;

    public static function init(SystemEnvHelper $helper): void
    {
        self::$helper = $helper;
    }

    private static function getHelper(): SystemEnvHelper
    {
        if (!self::$helper) {
            throw new \LogicException('SystemEnvFacade not initialized');
        }
        return self::$helper;
    }

    public static function get(string $key, $default = null)
    {
        return self::getHelper()->get($key, $default);
    }

    public static function all(): array
    {
        return self::getHelper()->all();
    }

    public static function isInitialized(): bool
    {
        return self::$helper !== null;
    }
}
