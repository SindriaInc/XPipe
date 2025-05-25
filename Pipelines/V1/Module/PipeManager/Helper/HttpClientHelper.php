<?php

namespace Pipelines\PipeManager\Helper;

use Zend\Http\Client;
use Zend\Http\Request;

class HttpClientHelper
{

    private $httpClient;
    public function __construct(Client $httpClient)
    {
        $this->httpClient = $httpClient;
    }

    public function get(string $uri, array $headers = [])
    {
        $this->httpClient->setUri($uri);
        $this->httpClient->setMethod(Request::METHOD_GET);
        $this->httpClient->setHeaders($headers);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();

        return $response->getBody();
    }


}