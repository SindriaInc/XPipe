<?php

namespace Sindria\OAuth2\Client\Adapter;

/**
 * Short description for DefaultAdapter.php
 *
 * @package DefaultAdapter
 * @author zhenyangze <zhenyangze@gmail.com>
 * @version 0.1
 * @copyright (C) 2021 zhenyangze <zhenyangze@gmail.com>
 * @license MIT
 */

class DefaultAdapter extends AdapterAbstract
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
        if (!session_id()) {
            session_start();
        }
        $sessionKey = 'token_' . md5($accessToken);
        return isset($_SESSION[$sessionKey]) ? @json_decode($_SESSION[$sessionKey], true) : ''; 
    }

    /**
     * {@inheritDoc}
     */
    public function saveToken($accessToken = '', $token, $time = 3600)
    {
        if (!session_id()) {
            session_start();
        }
        $sessionKey = 'token_' . md5($accessToken);
        $_SESSION[$sessionKey] = json_encode($token);
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
