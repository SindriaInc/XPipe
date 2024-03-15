<?php

namespace App\Traits;

use Illuminate\Support\Facades\Http;

trait Rest {

    /**
     * Get request
     *
     * @param string $endpoint
     * @return array|string
     */
    public function get($endpoint)
    {
        $url = env('API_GATEWAY_HOST') . $endpoint;
        $response = Http::get($url);
        return $response->body();
    }


    /**
     * Post request
     *
     * @param string $endpoint
     * @param array $data
     * @return array|string
     */
    public function post($endpoint, $data)
    {
        $url = env('API_GATEWAY_HOST') . $endpoint;
        $response = Http::post($url,$data);
        return $response->body();
    }


    /**
     * Put request
     *
     * @param string $endpoint
     * @param array $data
     * @return array|string
     */
    public function put($endpoint, $data)
    {
        $url = env('API_GATEWAY_HOST') . $endpoint;
        $response = Http::put($url,$data);
        return $response->body();
    }


    /**
     * Delete request
     *
     * @param string $endpoint
     * @param array $data
     * @return array|string
     */
    public function delete($endpoint, $data)
    {
        $url = env('API_GATEWAY_HOST') . $endpoint;
        $response = Http::delete($url,$data);
        return $response->body();
    }


    /**
     * Auth request
     *
     * @param string $endpoint
     * @param array|string $data
     * @return array|string
     */
    public function auth($endpoint, $data)
    {
        $url = env('API_GATEWAY_HOST') . $endpoint;
        $response = Http::withHeaders([
            //'Content-Type' => 'application/json',
            'X-Requested-With' => 'XMLHttpRequest'
        ])->post($url, $data);

        return $response->body();
    }


    /**
     * Get request with bearer token
     *
     * @param $token
     * @param $endpoint
     * @param $data
     * @return string
     */
    public function getWithToken($token, $endpoint)
    {
        $url = env('API_GATEWAY_HOST') . $endpoint;
        $response = Http::withToken($token)->get($url);
        return $response->body();
    }


    /**
     * Post request with bearer token
     *
     * @param $token
     * @param $endpoint
     * @param $data
     * @return string
     */
    public function postWithToken($token, $endpoint, $data = [])
    {
        $url = env('API_GATEWAY_HOST') . $endpoint;
        $response = Http::withToken($token)->post($url,$data);
        return $response->body();
    }


    /**
     * Put request with bearer token
     *
     * @param $token
     * @param $endpoint
     * @param $data
     * @return string
     */
    public function putWithToken($token, $endpoint, $data = [])
    {
        $url = env('API_GATEWAY_HOST') . $endpoint;
        $response = Http::withToken($token)->put($url,$data);
        return $response->body();
    }


    /**
     * Delete request with bearer token
     *
     * @param $token
     * @param $endpoint
     * @param $data
     * @return string
     */
    public function deleteWithToken($token, $endpoint, $data = [])
    {
        $url = env('API_GATEWAY_HOST') . $endpoint;
        $response = Http::withToken($token)->delete($url,$data);
        return $response->body();
    }



}
