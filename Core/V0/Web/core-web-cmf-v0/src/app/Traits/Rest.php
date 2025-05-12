<?php

namespace App\Traits;

use Illuminate\Support\Facades\Http;

trait Rest
{

    /**
     * Get request
     *
     * @param string $endpoint
     * @return string
     */
    public function get(string $endpoint) : string
    {
        $url = api_gateway_url() . $endpoint;
        $response = Http::get($url);
        return $response->body();
    }


    /**
     * Post request
     *
     * @param string $endpoint
     * @param array $data
     * @return string
     */
    public function post(string $endpoint, array $data) : string
    {
        $url = api_gateway_url() . $endpoint;
        $response = Http::post($url,$data);
        return $response->body();
    }


    /**
     * Put request
     *
     * @param string $endpoint
     * @param array $data
     * @return string
     */
    public function put(string $endpoint, array $data) : string
    {
        $url = api_gateway_url() . $endpoint;
        $response = Http::put($url,$data);
        return $response->body();
    }


    /**
     * Delete request
     *
     * @param string $endpoint
     * @param array $data
     * @return string
     */
    public function delete(string $endpoint, array $data) : string
    {
        $url = api_gateway_url() . $endpoint;
        $response = Http::delete($url,$data);
        return $response->body();
    }


    /**
     * Post request with an attachment
     *
     * @param string $endpoint
     * @param string $field
     * @param string $filepath
     * @param string $fileName
     * @param array $data
     * @return string
     */
    public function postWithAttachment(string $endpoint, string $field, string $filepath, string $fileName, array $data = []) : string
    {
        $url = api_gateway_url() . $endpoint;
        $response = Http::attach($field, file_get_contents($filepath), $fileName)->post($url, $data);
        return $response->body();
    }


    /**
     * Auth request
     *
     * @param string $endpoint
     * @param array $data
     * @return string
     */
    public function auth(string $endpoint, array $data) : string
    {
        $url = api_gateway_url() . $endpoint;
        $response = Http::withHeaders([
            //'Content-Type' => 'application/json',
            'X-Requested-With' => 'XMLHttpRequest'
        ])->post($url, $data);

        return $response->body();
    }


    /**
     * Get request with bearer token
     *
     * @param string $token
     * @param string $endpoint
     * @return string
     */
    public function getWithToken(string $token, string $endpoint) : string
    {
        $url = api_gateway_url() . $endpoint;
        $response = Http::withToken($token)->get($url);
        return $response->body();
    }


    /**
     * Post request with bearer token
     *
     * @param string $token
     * @param string $endpoint
     * @param array $data
     * @return string
     */
    public function postWithToken(string $token, string $endpoint, array $data = []) : string
    {
        $url = api_gateway_url() . $endpoint;
        $response = Http::withToken($token)->post($url,$data);
        return $response->body();
    }


    /**
     * Put request with bearer token
     *
     * @param string $token
     * @param string $endpoint
     * @param array $data
     * @return string
     */
    public function putWithToken(string $token, string $endpoint, array $data = []) : string
    {
        $url = api_gateway_url() . $endpoint;
        $response = Http::withToken($token)->put($url,$data);
        return $response->body();
    }


    /**
     * Delete request with bearer token
     *
     * @param string $token
     * @param string $endpoint
     * @param array $data
     * @return string
     */
    public function deleteWithToken(string $token, string $endpoint, array $data = []) : string
    {
        $url = api_gateway_url() . $endpoint;
        $response = Http::withToken($token)->delete($url,$data);
        return $response->body();
    }



}
