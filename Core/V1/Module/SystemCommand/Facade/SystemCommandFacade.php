<?php
namespace Core\SystemCommand\Facade;

use Core\SystemCommand\Helper\SystemCommandHelper;

class SystemCommandFacade
{
    private static ?SystemCommandHelper $helper = null;

    public static function init(SystemCommandHelper $helper): void
    {
        self::$helper = $helper;
    }

    private static function getHelper(): SystemCommandHelper
    {
        if (!self::$helper) {
            throw new \LogicException('SystemCommandFacade not initialized');
        }
        return self::$helper;
    }

    public static function run(string $cmd, array $args = []): string
    {
        return self::getHelper()->run($cmd, $args);
    }

    public static function isInitialized(): bool
    {
        return self::$helper !== null;
    }
}
