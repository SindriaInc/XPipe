<?php

namespace Sindria\OAuth2\Client\Adapter;

/**
 * Short description for AdapterAbstract.php
 *
 * @package AdapterAbstract
 * @author zhenyangze <zhenyangze@gmail.com>
 * @version 0.1
 * @copyright (C) 2021 zhenyangze <zhenyangze@gmail.com>
 * @license MIT
 */

/**
 * 
 */
abstract class AdapterAbstract
{
    /**
     * getAccessToken 
     *
     * @return 
     */
    abstract public function getAccessToken();
    /**
     * saveAccessToken 
     *
     * @return 
     */
    abstract public function saveAccessToken($accessToken = '', $time = 3600);

    /**
     * getToken 
     *
     * @param $accessToken
     *
     * @return 
     */
    abstract public function getToken($accessToken = '');
    /**
     * saveToken 
     *
     * @param $accessToken
     * @param $token
     * @param $time
     *
     * @return 
     */
    abstract public function saveToken($accessToken = '', $token, $time = 3600);

    /**
     * getCode 
     *
     * @return 
     */
    abstract public function getCode();

    /**
     * log 
     *
     * @param $e
     *
     * @return 
     */
    abstract public function log($e);
}
