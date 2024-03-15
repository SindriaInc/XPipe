<?php

namespace Sindria\OAuth2\Client\Adapter;

/**
 * Short description for DefaultAdapter.php
 *
 * @package CodeIgniterAdapter
 * @author zhenyangze <zhenyangze@gmail.com>
 * @version 0.1
 * @copyright (C) 2021 zhenyangze <zhenyangze@gmail.com>
 * @license MIT
 */

class CodeIgniterAdapter extends AdapterAbstract
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
        if (!function_exists('get_instance')) {
            return '';
        }

        $CI = &get_instance();
        $CI->load->driver('cache', array('adapter' => 'redis', 'backup' => 'file'));
        return $CI->cache->get($accessToken);
    }

    /**
     * {@inheritDoc}
     */
    public function saveToken($accessToken = '', $token, $time = 3600)
    {
        if (!function_exists('get_instance')) {
            return;
        }

        $CI = &get_instance();
        $CI->load->driver('cache', array('adapter' => 'redis', 'backup' => 'file'));
        return $CI->cache->save($accessToken, $token, $time);
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
