<?php
namespace Sindria\Logger\Facade;

use Magento\Framework\App\ObjectManager;
use Psr\Log\LoggerInterface;

class LoggerFacade
{
    protected static function logger(): LoggerInterface
    {
        return ObjectManager::getInstance()->get(LoggerInterface::class);
    }

    // Metodo magico per fallback automatico
    public static function __callStatic($name, $arguments)
    {
        $logger = self::logger();
        if (method_exists($logger, $name)) {
            return call_user_func_array([$logger, $name], $arguments);
        }

        throw new \BadMethodCallException("Method $name does not exist on logger");
    }

    // Metodi espliciti
    public static function debug($message, array $context = [])
    {
        self::logger()->debug($message, $context);
    }

    public static function info($message, array $context = [])
    {
        self::logger()->info($message, $context);
    }

    public static function notice($message, array $context = [])
    {
        self::logger()->notice($message, $context);
    }

    public static function warning($message, array $context = [])
    {
        self::logger()->warning($message, $context);
    }

    public static function error($message, array $context = [])
    {
        self::logger()->error($message, $context);
    }

    public static function critical($message, array $context = [])
    {
        self::logger()->critical($message, $context);
    }

    public static function alert($message, array $context = [])
    {
        self::logger()->alert($message, $context);
    }

    public static function emergency($message, array $context = [])
    {
        self::logger()->emergency($message, $context);
    }
}
