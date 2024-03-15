<?php

namespace Sindria\SsoKeycloak;

use Illuminate\Support\Facades\Http;
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
    public function createUserSession($user) : void
    {
        clean_user_cache($user->ID);
        wp_clear_auth_cookie();

        wp_set_current_user( $user->ID );
        wp_set_auth_cookie( $user->ID , true, false);

        update_user_caches($user);
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
