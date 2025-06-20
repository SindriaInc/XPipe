<?php
namespace Core\QueryBuilder\Facade;

use Core\QueryBuilder\Helper\QueryBuilderHelper;

class QueryFacade
{
    private static ?QueryBuilderHelper $client = null;

    public static function isInitialized(): bool
    {
        return self::$client !== null;
    }

    public static function init(QueryBuilderHelper $helper): void
    {
        self::$client = $helper;
    }

    protected static function client(): QueryBuilderHelper
    {
        if (!self::$client) {
            throw new \LogicException("QueryFacade not initialized.");
        }
        return self::$client;
    }

    public static function __callStatic($name, $arguments)
    {
        $client = self::client();
        if (method_exists($client, $name)) {
            return call_user_func_array([$client, $name], $arguments);
        }
        throw new \BadMethodCallException("Method $name does not exist on client");
    }

    public static function query(string $table, $sql, array $params = []): \Zend_Db_Statement_Interface
    {
        return self::client()->query($table, $sql, $params);
    }

    public static function simpleQuery(string $table, $sql): \Zend_Db_Statement_Interface
    {
        return self::client()->simpleQuery($table, $sql);
    }

    public static function manualPrepare(string $table, $sql, array $params = []): \Zend_Db_Statement_Interface
    {
        return self::client()->manualPrepare($table, $sql, $params);
    }


}
