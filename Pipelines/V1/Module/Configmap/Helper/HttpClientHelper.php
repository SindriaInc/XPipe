<?php

namespace Pipelines\Configmap\Helper;

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
     * Get request
     *
     * @param string $uri
     * @param array $headers
     * @return \Laminas\Http\Response
     */
    public function get(string $uri, array $headers = [], array $params = [])
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
    public function post(string $uri, array $headers = [], array $payload = [])
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
     * @return string
     */
    public function put(string $uri, array $headers = [], array $payload = [])
    {
        $this->httpClient->setUri($uri);
        $this->httpClient->setMethod(Request::METHOD_PUT);
        $this->httpClient->setHeaders($headers);
        $this->httpClient->setParameterPost($payload);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();
        return $response->getBody();
    }

    /**
     * Delete request
     *
     * @param string $uri
     * @param array $headers
     * @return string
     */
    public function delete(string $uri, array $headers = [])
    {
        $this->httpClient->setUri($uri);
        $this->httpClient->setMethod(Request::METHOD_DELETE);
        $this->httpClient->setHeaders($headers);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();
        return $response->getBody();
    }

    /**
     * Post raw request
     *
     * @param string $uri
     * @param array $headers
     * @param string $payload
     * @return string
     */
    public function postRaw(string $uri, array $headers = [], string $payload = "")
    {
        $this->httpClient->setUri($uri);
        $this->httpClient->setMethod(Request::METHOD_POST);
        $this->httpClient->setHeaders($headers);
        $this->httpClient->setRawBody($payload);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();
        return $response->getBody();
    }

    /**
     * Put raw request
     *
     * @param string $uri
     * @param array $headers
     * @param string $payload
     * @return string
     */
    public function putRaw(string $uri, array $headers = [], string $payload = "")
    {
        $this->httpClient->setUri($uri);
        $this->httpClient->setMethod(Request::METHOD_PUT);
        $this->httpClient->setHeaders($headers);
        $this->httpClient->setRawBody($payload);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();
        return $response->getBody();
    }


    /**
     * Delete raw request
     *
     * @param string $uri
     * @param array $headers
     * @param string $payload
     * @return string
     */
    public function deleteRaw(string $uri, array $headers = [], string $payload = "")
    {
        $this->httpClient->setUri($uri);
        $this->httpClient->setMethod(Request::METHOD_DELETE);
        $this->httpClient->setHeaders($headers);
        $this->httpClient->setRawBody($payload);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();
        return $response->getBody();
    }


}