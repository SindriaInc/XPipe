<?php

namespace Core\Http\Helper;

use Laminas\Http\Client;
use Laminas\Http\Request;

class HttpClientHelper
{
    private Client $httpClient;

    /**
     * @param Client $httpClient
     */
    public function __construct(Client $httpClient)
    {
        $this->httpClient = $httpClient;
    }

    /**
     * Singleton instance
     *
     * @var object $instance
     */
    private static object $instance;

    /**
     * Get singleton instance
     *
     * @return \Core\Http\Helper\HttpClientHelper
     */
    public static function getInstance() : \Core\Http\Helper\HttpClientHelper
    {
        if (!isset(self::$instance)) {
            $className = __CLASS__;
            self::$instance = new $className(new Client());
        }

        return self::$instance;
    }


    /**
     * Get request
     *
     * @param string $uri
     * @param array $headers
     * @return \Laminas\Http\Response
     */
    public function get(string $uri, array $headers = [], array $params = []) : \Laminas\Http\Response
    {
        $this->httpClient->setUri($uri);
        $this->httpClient->setMethod(Request::METHOD_GET);
        $this->httpClient->setHeaders($headers);
        $this->httpClient->setParameterGet($params);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();
        return $response;
    }


    /**
     * Post request
     *
     * @param string $uri
     * @param array $headers
     * @param array $payload
     * @return \Laminas\Http\Response
     */
    public function post(string $uri, array $headers = [], array $payload = []) : \Laminas\Http\Response
    {
        $this->httpClient->setUri($uri);
        $this->httpClient->setMethod(Request::METHOD_POST);
        $this->httpClient->setHeaders($headers);
        $this->httpClient->setParameterPost($payload);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();

        return $response;
    }

    /**
     * Put request
     *
     * @param string $uri
     * @param array $headers
     * @param array $payload
     * @return \Laminas\Http\Response
     */
    public function put(string $uri, array $headers = [], array $payload = []) : \Laminas\Http\Response
    {
        $this->httpClient->setUri($uri);
        $this->httpClient->setMethod(Request::METHOD_PUT);
        $this->httpClient->setHeaders($headers);
        $this->httpClient->setParameterPost($payload);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();
        return $response;
    }

    /**
     * Delete request
     *
     * @param string $uri
     * @param array $headers
     * @return \Laminas\Http\Response
     */
    public function delete(string $uri, array $headers = []) : \Laminas\Http\Response
    {
        $this->httpClient->setUri($uri);
        $this->httpClient->setMethod(Request::METHOD_DELETE);
        $this->httpClient->setHeaders($headers);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();
        return $response;
    }

    /**
     * Post raw request
     *
     * @param string $uri
     * @param array $headers
     * @param string $payload
     * @return \Laminas\Http\Response
     */
    public function postRaw(string $uri, array $headers = [], string $payload = "") : \Laminas\Http\Response
    {
        $this->httpClient->setUri($uri);
        $this->httpClient->setMethod(Request::METHOD_POST);
        $this->httpClient->setHeaders($headers);
        $this->httpClient->setRawBody($payload);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();
        return $response;
    }

    /**
     * Put raw request
     *
     * @param string $uri
     * @param array $headers
     * @param string $payload
     * @return \Laminas\Http\Response
     */
    public function putRaw(string $uri, array $headers = [], string $payload = "") : \Laminas\Http\Response
    {
        $this->httpClient->setUri($uri);
        $this->httpClient->setMethod(Request::METHOD_PUT);
        $this->httpClient->setHeaders($headers);
        $this->httpClient->setRawBody($payload);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();
        return $response;
    }


    /**
     * Delete raw request
     *
     * @param string $uri
     * @param array $headers
     * @param string $payload
     * @return \Laminas\Http\Response
     */
    public function deleteRaw(string $uri, array $headers = [], string $payload = "") : \Laminas\Http\Response
    {
        $this->httpClient->setUri($uri);
        $this->httpClient->setMethod(Request::METHOD_DELETE);
        $this->httpClient->setHeaders($headers);
        $this->httpClient->setRawBody($payload);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();
        return $response;
    }


}