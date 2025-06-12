<?php
namespace Core\QueryBuilder\Facade;

use Core\QueryBuilder\Helper\QueryBuilderHelper;

class QueryFacade
{
    protected static function client(): QueryBuilderHelper
    {
        return QueryBuilderHelper::getInstance();
    }

    // Metodo magico per fallback automatico
    public static function __callStatic($name, $arguments)
    {
        $client = self::client();
        if (method_exists($client, $name)) {
            return call_user_func_array([$client, $name], $arguments);
        }

        throw new \BadMethodCallException("Method $name does not exist on client");
    }


    public static function query(string $table, $sql) : \Zend_Db_Statement_Interface
    {
        return self::client()->query($table, $sql);
    }



}
