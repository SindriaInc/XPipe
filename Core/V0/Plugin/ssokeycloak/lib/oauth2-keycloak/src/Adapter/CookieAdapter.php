<?php

namespace Sindria\OAuth2\Client\Adapter;

/**
 * Short description for DefaultAdapter.php
 *
 * @package CookieAdapter 
 * @author zhenyangze <zhenyangze@gmail.com>
 * @version 0.1
 * @copyright (C) 2021 zhenyangze <zhenyangze@gmail.com>
 * @license MIT
 */

class CookieAdapter extends AdapterAbstract
{
    /**
     * {@inheritDoc}
     */
    public function getAccessToken()
    {
        return isset($_COOKIE['token']) ? $_COOKIE['token'] : '';
    }

    /**
     * {@inheritDoc}
     */
    public function saveAccessToken($accessToken = '', $time = 3600)
    {
        setcookie('token', $accessToken, [
            'path' => '/',
            'expires' => time() + $time,
        ]);
    }

    /**
     * {@inheritDoc}
     */
    public function getToken($accessToken = '')
    {
        return '';
    }

    /**
     * {@inheritDoc}
     */
    public function saveToken($accessToken = '', $token, $time = 3600)
    {
        return;
    }

    /**
     * {@inheritDoc}
     */
    public function getCode()
    {
        return isset($_GET['code']) ? $_GET['code'] : '';
    }

    /**
     * {@inheritDoc}
     */
    public function log($e)
    {
    }
}
