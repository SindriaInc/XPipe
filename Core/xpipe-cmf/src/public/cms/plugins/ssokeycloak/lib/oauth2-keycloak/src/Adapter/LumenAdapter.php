<?php

namespace Sindria\OAuth2\Client\Adapter;

use Illuminate\Support\Facades\Cookie;
use Cache;
use Illuminate\Support\Facades\Log;

/**
 * Short description for LumenAdapter.php
 *
 * @package LumenAdapter
 * @author zhenyangze <zhenyangze@gmail.com>
 * @version 0.1
 * @copyright (C) 2021 zhenyangze <zhenyangze@gmail.com>
 * @license MIT
 */

class LumenAdapter extends AdapterAbstract
{
    /**
     * {@inheritDoc}
     */
    public function getAccessToken()
    {
        return request()->bearerToken();
    }

    /**
     * {@inheritDoc}
     */
    public function saveAccessToken($accessToken = '', $time = 60)
    {
        request()->headers->add([
            'New-Token' => $accessToken,
        ]);
    }

    /**
     * {@inheritDoc}
     */
    public function getToken($accessToken = '')
    {
        return @json_decode(Cache::get($accessToken), JSON_OBJECT_AS_ARRAY);
    }

    /**
     * {@inheritDoc}
     */
    public function saveToken($accessToken = '', $token, $time = 3600)
    {
        Cache::put($accessToken, json_encode($token), $time);
    }

    /**
     * {@inheritDoc}
     */
    public function getCode()
    {
        return request()->get('code');
    }

    /**
     * {@inheritDoc}
     */
    public function log($e)
    {
        if ($e instanceof \Exception) {
            Log::error($e->getMessage());
        } else if (is_string($e)) {
            Log::error($e);
        }
    }
}
