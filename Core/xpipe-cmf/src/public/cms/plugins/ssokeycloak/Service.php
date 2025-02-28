<?php

namespace Sindria\SsoKeycloak;

use Sindria\OAuth2\Client\Passport;
use Sindria\SsoKeycloak\Helper;

use GuzzleHttp6\Client;
use GuzzleHttp6\Psr7\Request;

class Service
{
    private Client $client;

    /**
     * Service constructor
     */
    public function __construct()
    {
        $this->client = new Client();
    }


    /**
     * Create user session into WordPress
     *
     * @param $user
     * @return void
     */
    public function createUserSession($user, $uuid, $accessToken) : void
    {
        clean_user_cache($user->ID);
        wp_clear_auth_cookie();

        wp_set_current_user( $user->ID );
        //wp_set_auth_cookie( $user->ID , true, false);
        wp_set_auth_cookie( $user->ID , true);

        update_user_caches($user);

        $this->saveUser($user);
        $this->saveUserMetaOnDatabase($user->ID, $uuid, $accessToken);
    }

    /**
     * Save current user data on session
     *
     * @param $user
     * @return void
     */
    private function saveUser($user) : void
    {
        if (!session_id()) {
            session_start();
        }

        $sessionKey = 'user_' . md5($user->user_login);
        $_SESSION[$sessionKey] = json_encode($user);
    }

    /**
     * Save current user meta data on database
     *
     * @param $userId
     * @param $uuid
     * @param $accessToken
     * @return void
     */
    private function saveUserMetaOnDatabase($userId, $uuid, $accessToken)
    {
        $hasUuid = metadata_exists( 'user', $userId, 'uuid');

        if ($hasUuid) {
            update_user_meta($userId, 'uuid', $uuid);
        } else {
            add_user_meta($userId, 'uuid', $uuid, false);
        }

        $tokenObject = $this->getTokenObject($accessToken);

        $access_token = $tokenObject['access_token'];
        $expires = $tokenObject['expires'];
        $refresh_token = $tokenObject['refresh_token'];
        $refresh_expires_in = $tokenObject['refresh_expires_in'];

        // Access Token
        $this->saveUserMeta($userId, 'access_token', $access_token);
        // Expires
        $this->saveUserMeta($userId, 'expires', $expires);
        // Refresh Token
        $this->saveUserMeta($userId, 'refresh_token', $refresh_token);
        // Refresh Expires In
        $this->saveUserMeta($userId, 'refresh_expires_in', $refresh_expires_in);

        $expires_at = $expires + 3600 - time();
        $refresh_at = gmdate("Y-m-d\TH:i:s\Z", $expires);
        $refresh_counter = 0;

        // Expires At
        $this->saveUserMeta($userId, 'expires_at', $expires_at);
        // Refresh At
        $this->saveUserMeta($userId, 'refresh_at', $refresh_at);

        // Refresh Counter
        $hasRefreshCounter = metadata_exists( 'user', $userId, 'refresh_counter');

        if (! $hasRefreshCounter) {
            add_user_meta($userId, 'refresh_counter', $refresh_counter, false);
        }
    }

    /**
     * Save user meta with check
     *
     * @param $userId
     * @param $metaKey
     * @param $metaValue
     * @return void
     */
    private function saveUserMeta($userId, $metaKey, $metaValue)
    {
        $hasMeta = metadata_exists( 'user', $userId, $metaKey);

        if ($hasMeta) {
            update_user_meta($userId, $metaKey, $metaValue);
        } else {
            add_user_meta($userId, $metaKey, $metaValue, false);
        }
    }


    /**
     * Get current token object from $_SESSION
     *
     * @param $accessToken
     * @return mixed|string
     */
    private function getTokenObject($accessToken = '')
    {
        if (!session_id()) {
            session_start();
        }
        $sessionKey = 'token_' . md5($accessToken);
        return isset($_SESSION[$sessionKey]) ? @json_decode($_SESSION[$sessionKey], true) : '';
    }


    /**
     * Create user profile on local DB
     *
     * @param string $username
     * @param string $email
     * @param string $name
     * @param string $surname
     * @return int
     */
    public function createUserProfile(string $username, string $email, string $name, string $surname) : int
    {
        $userId = wp_insert_user( array(
            'user_login' => $username,
            'user_pass' => '',
            'user_nicename' => $username,
            'user_email' => $email,
            'first_name' => $name,
            'last_name' => $surname,
            'display_name' => $name . ' ' . $surname,
            'role' => 'operator'
        ));

        return $userId;
    }

    /**
     * Destroy current session data
     *
     * @return void
     */
    public function destroyUserSession() : void
    {
        foreach ($_SESSION as $key => $token) {
            unset($_SESSION[$key]);
        }
    }

    public function logout()
    {
        try {
            $form = [];
            $form['client_id'] = Helper::getAuthClientId();
            $form['client_secret'] = Helper::getAuthClientSecret();
            $form['refresh_token'] = Helper::getCurrentRefreshToken();

            $headers = [];
            $headers['Content-Type'] = 'application/x-www-form-urlencoded';

            $url = Helper::getAuthBaseUrl().'/auth/realms/'.Helper::getAuthRealm().'/protocol/openid-connect/logout';

            $response = $this->client->post($url, [
                'headers' => $headers,
                'form_params' => $form
            ]);

            $result = (string)$response->getBody();
            $resource = json_decode($result);

            $data = [];
            $data['logout'] = $resource;

            return $data;
        } catch (\Exception $e) {
            report($e);
            return false;
        }
    }

    public function revokeCurrentAccessToken()
    {
        try {
            $form = [];
            $form['client_id'] = Helper::getAuthClientId();
            $form['client_secret'] = Helper::getAuthClientSecret();
            $form['token'] = Helper::getCurrentAccessToken();

            $headers = [];
            $headers['Content-Type'] = 'application/x-www-form-urlencoded';

            $url = Helper::getAuthBaseUrl().'/auth/realms/'.Helper::getAuthRealm().'/protocol/openid-connect/revoke';

            $response = $this->client->post($url, [
                'headers' => $headers,
                'form_params' => $form
            ]);

            $result = (string)$response->getBody();
            $resource = json_decode($result);

            $data = [];
            $data['revoke'] = $resource;

            return $data;
        } catch (\Exception $e) {
            report($e);
            return false;
        }
    }
}
