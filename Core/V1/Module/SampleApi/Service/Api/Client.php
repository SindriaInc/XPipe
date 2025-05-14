<?php

namespace Core\SampleApi\Service\Api;

use Zend\Http\Client as HttpClient;
use Zend\Http\Request;

class Client
{
    private string $baseUri = 'https://api.restful-api.dev/objects';

    private function createClient(string $uri, string $method, array $data = []): HttpClient
    {
        $client = new HttpClient();
        $client->setUri($uri);
        $client->setMethod($method);
        $client->setHeaders(['Content-Type' => 'application/json']);
        $client->setOptions(['timeout' => 10]);

        if (!empty($data)) {
            $client->setRawBody(json_encode($data));
        }

        return $client;
    }

    public function getAll(): array
    {
        return $this->request("$this->baseUri", Request::METHOD_GET);
    }

    public function get(string $id): array
    {
        return $this->request("$this->baseUri/$id", Request::METHOD_GET);
    }

    public function create(array $data): array
    {
        return $this->request($this->baseUri, Request::METHOD_POST, $data);
    }

    public function update(string $id, array $data): array
    {
        return $this->request("$this->baseUri/$id", Request::METHOD_PUT, $data);
    }

    public function delete(string $id): array
    {
        return $this->request("$this->baseUri/$id", Request::METHOD_DELETE);
    }

    private function request(string $uri, string $method, array $data = []): array
    {
        try {
            $client = $this->createClient($uri, $method, $data);
            $response = $client->send();

            if ($response->isSuccess()) {
                return [
                    'success' => true,
                    'data' => json_decode($response->getBody(), true)
                ];
            }

            return [
                'success' => false,
                'error' => $response->getStatusCode() . ' - ' . $response->getReasonPhrase() . ' - ' . $response->getBody()
            ];
        } catch (\Exception $e) {
            return [
                'success' => false,
                'error' => 'Exception: ' . $e->getMessage()
            ];
        }
    }
}
