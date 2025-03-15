<?php

namespace App\Http\Controllers\Api;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;

class ProxyController extends ApiController
{

    /**
     * Handle API proxy
     *
     * @param Request $request
     */
    public function handle(Request $request)
    {
        $token = NULL;

        if ($request->headers->has('authorization')) {
            $token = $request->header('authorization');
        }

        return $this->makeRequest($request->method(), $request->getRequestUri(), $request->query(), $request->input(), $request->headers, $request->file(), $token);
    }


    /**
     * Make request to any service
     *
     * @param string $method
     * @param string $uri
     * @param array $query
     * @param array $data
     * @param array $headers
     * @param array $file
     * @param string|null $token
     */
    public function makeRequest($method, $uri, $query = [], $data = [], $headers = [], $file = [], $token = NULL)
    {
        $service = $this->matchService($uri);

        $url = $this->makeUrlRequest($service, $uri);

        // Only for auth token request
        if ($service == "auth") {
            return $this->auth($url, $data);
        }


        switch ($method) {
            case "GET":
                $request = $this->get($url, $query, $data, $headers, $file, $token);
                break;
            case "POST":
                $request = $this->post($url, $query, $data, $headers, $file, $token);
                break;
            case "PUT":
                $request = $this->put($url, $query, $data, $headers, $file, $token);
                break;
            case "DELETE":
                $request = $this->delete($url, $query, $data, $headers, $file, $token);
                break;
        }

        return $request;
    }


    /**
     * Match service callback by uri
     *
     * @param string $uri
     * @return string
     */
    public function matchService($uri) : string
    {
        $current = explode('/', $uri);
        return $current['3'];
    }


    /**
     * Make full url for request
     *
     * @param string $service
     * @param string $uri
     * @return string
     */
    public function makeUrlRequest($service, $uri)
    {
        $current = explode('/', $uri);

        // WP json API only for WP as blog
        if ($service == "blog" && $current['4'] == "wp-json") {
            $uri = substr($uri, strlen('/api/v1/blog'));
        }

        // WP json API only for WP as blog it
        if ($service == "blog_it" && $current['4'] == "wp-json") {
            $uri = substr($uri, strlen('/api/v1/blog_it'));
        }

        // WP json API only for WP as gallery
        if ($service == "gallery" && $current['4'] == "wp-json") {
            $uri = substr($uri, strlen('/api/v1/gallery'));
	    }

	    // WP json API only for WP as pages
        if ($service == "pages" && $current['4'] == "wp-json") {
            $uri = substr($uri, strlen('/api/v1/pages'));
	    }

	    // WP json API only for WP as cms
        if ($service == "cms" && $current['4'] == "wp-json") {
            $uri = substr($uri, strlen('/api/v1/cms'));
        }


        $base_url = env('APP_URL');

        $match = strtoupper($service);
        $suffix = '_SERVICE';

        $env = $match . $suffix;

        if (env($env)) {
            $base_url = env($env);
        }

        return $base_url . $uri;
    }


    /**
     * Get request
     *
     * @param string $url
     * @param array $query
     * @param array $data
     * @param array $headers
     * @param array $file
     * @param string|null $token
     * @return array|string
     */
    public function get($url, $query, $data, $headers, $file, $token = NULL)
    {
        if ($token !== NULL) {
            // Extract only token value without Bearer
            if (strpos($token, 'Bearer ') !== false) {
                $value = substr($token, strlen('Bearer '));
            }

            $response = Http::withToken($value)->get($url, $query);
        } else {
            $response = Http::get($url, $query);
        }

        return $response->json();
    }


    /**
     * Post request
     *
     * @param string $url
     * @param array $query
     * @param array $data
     * @param array $headers
     * @param array $file
     * @param string|null $token
     * @return string
     */
    public function post($url, $query, $data, $headers, $file, $token = NULL)
    {
        if ($token !== NULL) {
            // Extract only token value without Bearer
            if (strpos($token, 'Bearer ') !== false) {
                $value = substr($token, strlen('Bearer '));
            }

            $response = Http::withToken($value)->post($url, $data);
        } else {
            $response = Http::post($url, $data);
        }

        return $response->json();
    }


    /**
     * Put request
     *
     * @param string $url
     * @param array $query
     * @param array $data
     * @param array $headers
     * @param array $file
     * @param string|null $token
     * @return string
     */
    public function put($url, $query, $data, $headers, $file, $token = NULL)
    {
        if ($token !== NULL) {
            // Extract only token value without Bearer
            if (strpos($token, 'Bearer ') !== false) {
                $value = substr($token, strlen('Bearer '));
            }

            $response = Http::withToken($value)->put($url, $data);
        } else {
            $response = Http::put($url, $data);
        }

        return $response->json();
    }


    /**
     * Delete request
     *
     * @param string $url
     * @param array $query
     * @param array $data
     * @param array $headers
     * @param array $file
     * @param string|null $token
     * @return string
     */
    public function delete($url, $query, $data, $headers, $file, $token = NULL)
    {
        if ($token !== NULL) {
            // Extract only token value without Bearer
            if (strpos($token, 'Bearer ') !== false) {
                $value = substr($token, strlen('Bearer '));
            }

            $response = Http::withToken($value)->delete($url, $data);
        } else {
            $response = Http::delete($url, $data);
        }

        return $response->json();
    }


    /**
     * Auth request
     *
     * @param string $url
     * @param array|string $data
     * @return array|string
     */
    public function auth($url, $data)
    {
        $response = Http::withHeaders([
            //'Content-Type' => 'application/json',
            'X-Requested-With' => 'XMLHttpRequest'
        ])->post($url, $data);

        return $response->json();
    }


}
