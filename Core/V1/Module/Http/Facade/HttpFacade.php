<?php
namespace Core\Http\Facade;

use Core\Http\Helper\HttpClientHelper;

class HttpFacade
{
    protected static function client(): HttpClientHelper
    {
        return HttpClientHelper::getInstance();
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

    // Metodi espliciti
    public static function get(string $uri, array $headers = [], array $params = [])
    {
        self::client()->get($uri, $headers, $params);
    }

    public static function post(string $uri, array $headers = [], array $payload = [])
    {
        self::client()->post($uri, $headers, $payload);
    }

    public static function put(string $uri, array $headers = [], array $payload = [])
    {
        self::client()->put($uri, $headers, $payload);
    }

    public static function delete(string $uri, array $headers = [])
    {
        self::client()->delete($uri, $headers);
    }

    public static function postRaw(string $uri, array $headers = [], string $payload = "")
    {
        self::client()->postRaw($uri, $headers, $payload);
    }

    public static function putRaw(string $uri, array $headers = [], string $payload = "")
    {
        self::client()->putRaw($uri, $headers, $payload);
    }

    public static function deleteRaw(string $uri, array $headers = [], string $payload = "")
    {
        self::client()->deleteRaw($uri, $headers, $payload);
    }


}
