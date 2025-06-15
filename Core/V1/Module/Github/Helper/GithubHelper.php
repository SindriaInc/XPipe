<?php

namespace Core\Github\Helper;

use Laminas\Http\Client;
use Laminas\Http\Request;
use Laminas\Http\Response;

class GithubHelper
{
    private Client $httpClient;

    private const GITHUB_API_BASE_URL = "https://api.github.com";

    private string $token;


    private array $headers;

    /**
     * @param Client $httpClient
     */
    public function __construct(Client $httpClient)
    {
        $this->httpClient = $httpClient;
        $this->token = SystemEnvHelper::get('CORE_GITHUB_ACCESS_TOKEN');

        $this->headers = [
            "Content-Type" => "application/json",
            "X-GitHub-Api-Version" => "2022-11-28",
            "Authorization" => "Bearer " . $this->token,
        ];
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
     * @return \Core\Github\Helper\GithubHelper
     */
    public static function getInstance() : \Core\Github\Helper\GithubHelper
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
     * @return \Laminas\Http\Response
     */
    public function get(string $uri, array $params = []) : \Laminas\Http\Response
    {
        $this->httpClient->setUri(self::GITHUB_API_BASE_URL . '/' . $uri);
        $this->httpClient->setMethod(Request::METHOD_GET);
        $this->httpClient->setHeaders($this->headers);
        $this->httpClient->setParameterGet($params);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();
        return $response;
    }


    /**
     * GET request that handles 302 redirect and returns response body
     *
     * @param string $uri
     * @return \Laminas\Http\Response|void
     */
    public function getForLogs(string $uri): \Laminas\Http\Response
    {
        try {
            $this->httpClient->reset(); // Reset client state
            $this->httpClient->setUri(self::GITHUB_API_BASE_URL . '/' . ltrim($uri, '/'));
            $this->httpClient->setMethod(Request::METHOD_GET);
            $this->httpClient->setHeaders($this->headers);
            $this->httpClient->setOptions(['timeout' => 10]);

            $response = $this->httpClient->send();

            // Handle 302 redirect manually
            if ($response->getStatusCode() === 302) {
                $redirectUrl = $response->getHeaders()->get('Location')->getFieldValue();

                if ($redirectUrl) {
                    $this->httpClient->reset();
                    $this->httpClient->setUri($redirectUrl);
                    $this->httpClient->setMethod(Request::METHOD_GET);
                    // Often redirected URLs are public or don't need headers, but keep them just in case
                    $this->httpClient->setHeaders($this->headers);
                    $this->httpClient->setOptions(['timeout' => 10]);

                    $response = $this->httpClient->send();
                }
            }

            return $response;
        } catch (\Exception $e) {
            // Log exception if needed
            error_log("GitHubHelper error in getForLogs: " . $e->getMessage());
//            return new Response();
        }
    }


    /**
     * Post request
     *
     * @param string $uri
     * @param array $payload
     * @return \Laminas\Http\Response
     */
    public function post(string $uri, array $payload = []) : \Laminas\Http\Response
    {
        $this->httpClient->setUri(self::GITHUB_API_BASE_URL . '/' . $uri);
        $this->httpClient->setMethod(Request::METHOD_POST);
        $this->httpClient->setHeaders($this->headers);
        $this->httpClient->setParameterPost($payload);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();

        return $response;
    }

    /**
     * Put request
     *
     * @param string $uri
     * @param array $payload
     * @return \Laminas\Http\Response
     */
    public function put(string $uri, array $payload = []) : \Laminas\Http\Response
    {
        $this->httpClient->setUri(self::GITHUB_API_BASE_URL . '/' . $uri);
        $this->httpClient->setMethod(Request::METHOD_PUT);
        $this->httpClient->setHeaders($this->headers);
        $this->httpClient->setParameterPost($payload);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();
        return $response;
    }

    /**
     * Patch request
     *
     * @param string $uri
     * @param array $payload
     * @return \Laminas\Http\Response
     */
    public function patch(string $uri, array $payload = []) : \Laminas\Http\Response
    {
        $this->httpClient->setUri(self::GITHUB_API_BASE_URL . '/' . $uri);
        $this->httpClient->setMethod(Request::METHOD_PATCH);
        $this->httpClient->setHeaders($this->headers);
        $this->httpClient->setParameterPost($payload);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();
        return $response;
    }

    /**
     * Delete request
     *
     * @param string $uri
     * @return \Laminas\Http\Response
     */
    public function delete(string $uri) : \Laminas\Http\Response
    {
        $this->httpClient->setUri(self::GITHUB_API_BASE_URL . '/' . $uri);
        $this->httpClient->setMethod(Request::METHOD_DELETE);
        $this->httpClient->setHeaders($this->headers);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();
        return $response;
    }

    /**
     * Post raw request
     *
     * @param string $uri
     * @param string $payload
     * @return \Laminas\Http\Response
     */
    public function postRaw(string $uri, string $payload = "") : \Laminas\Http\Response
    {
        $this->httpClient->setUri(self::GITHUB_API_BASE_URL . '/' . $uri);
        $this->httpClient->setMethod(Request::METHOD_POST);
        $this->httpClient->setHeaders($this->headers);
        $this->httpClient->setRawBody($payload);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();
        return $response;
    }

    /**
     * Put raw request
     *
     * @param string $uri
     * @param string $payload
     * @return \Laminas\Http\Response
     */
    public function putRaw(string $uri, string $payload = "") : \Laminas\Http\Response
    {
        $this->httpClient->setUri(self::GITHUB_API_BASE_URL . '/' . $uri);
        $this->httpClient->setMethod(Request::METHOD_PUT);
        $this->httpClient->setHeaders($this->headers);
        $this->httpClient->setRawBody($payload);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();
        return $response;
    }


    /**
     * Patch raw request
     *
     * @param string $uri
     * @param string $payload
     * @return \Laminas\Http\Response
     */
    public function patchRaw(string $uri, string $payload = "") : \Laminas\Http\Response
    {
        $this->httpClient->setUri(self::GITHUB_API_BASE_URL . '/' . $uri);
        $this->httpClient->setMethod(Request::METHOD_PATCH);
        $this->httpClient->setHeaders($this->headers);
        $this->httpClient->setRawBody($payload);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();
        return $response;
    }




    /**
     * Delete raw request
     *
     * @param string $uri
     * @param string $payload
     * @return \Laminas\Http\Response
     */
    public function deleteRaw(string $uri, string $payload = "") : \Laminas\Http\Response
    {
        $this->httpClient->setUri(self::GITHUB_API_BASE_URL . '/' . $uri);
        $this->httpClient->setMethod(Request::METHOD_DELETE);
        $this->httpClient->setHeaders($this->headers);
        $this->httpClient->setRawBody($payload);
        $this->httpClient->setOptions(['timeout' => 10]);

        $response = $this->httpClient->send();
        return $response;
    }


}