<?php

namespace Core\MicroFramework\Service;

use Core\Http\Facade\HttpFacade;

abstract class KeycloakService
{

    protected string $keycloakBaseUrl;
    protected string $keycloakRealm;
    protected string $keycloakClientId;
    protected string $keycloakClientSecret;
    protected string $keycloakAdminRealm;
    protected string $keycloakAdminClientId;
    protected string $keycloakAdminClientSecret;
    protected string $keycloakAdminUsername;
    protected string $keycloakAdminUPassword;



    private const API_KEYCLOAK_LOGGED_USER_SESSION = '%s/auth/realms/%s/protocol/openid-connect/token/introspect';
    private const API_KEYCLOAK_LOGGED_USER_INFO = '%s/auth/realms/%s/protocol/openid-connect/userinfo';
    private const API_KEYCLOAK_LOGIN = '%s/auth/realms/%s/protocol/openid-connect/token';
    private const API_KEYCLOAK_LOGOUT = '%s/auth/realms/%s/protocol/openid-connect/revoke';
    private const API_KEYCLOAK_LIST_USERS = '%s/auth/admin/realms/%s/users';
    private const API_KEYCLOAK_GET_USER_BY_ID = '%s/auth/admin/realms/%s/users/%s';
    private const API_KEYCLOAK_CREATE_USER = '%s/auth/admin/realms/%s/users';
    private const API_KEYCLOAK_EDIT_USER = '%s/auth/admin/realms/%s/users/%s';
    private const API_KEYCLOAK_DELETE_USER = '%s/auth/admin/realms/%s/users/%s';
    private const API_KEYCLOAK_SEARCH_USERS = '%s/auth/admin/realms/%s/users?search=%s';




    public function __construct(
        string $keycloakBaseUrl,
        string $keycloakRealm,
        string $keycloakClientId,
        string $keycloakClientSecret,
        string $keycloakAdminRealm,
        string $keycloakAdminClientId,
        string $keycloakAdminClientSecret,
        string $keycloakAdminUsername,
        string $keycloakAdminUPassword
    )
    {
        $this->keycloakBaseUrl = $keycloakBaseUrl;
        $this->keycloakRealm = $keycloakRealm;
        $this->keycloakClientId = $keycloakClientId;
        $this->keycloakClientSecret = $keycloakClientSecret;
        $this->keycloakAdminRealm = $keycloakAdminRealm;
        $this->keycloakAdminClientId = $keycloakAdminClientId;
        $this->keycloakAdminClientSecret = $keycloakAdminClientSecret;
        $this->keycloakAdminUsername = $keycloakAdminUsername;
        $this->keycloakAdminUPassword = $keycloakAdminUPassword;


    }

    public function keycloakLogin(string $username, string $password) : array
    {
        $uri = sprintf(self::API_KEYCLOAK_LOGIN, $this->keycloakBaseUrl, $this->keycloakRealm);
        $headers = [
            'Content-Type' => 'application/x-www-form-urlencoded'
        ];

        $form = [];
        $form['username'] = $username;
        $form['password'] = $password;
        $form['client_id'] = $this->keycloakClientId;
        $form['client_secret'] = $this->keycloakClientSecret;
        $form['grant_type'] = 'password';

        $response = HttpFacade::post($uri, $headers, $form);
        $resource = json_decode($response->getBody());


        if (isset($resource->error)) {
            $result['success'] = false;
            $result['data']['error'] = $resource->error;
            $result['data']['error_description'] = $resource->error_description;
            return $result;
        }

        $data = [];
        $data['access_token'] = $resource->access_token;
        $data['expires_in'] = $resource->expires_in;
        $data['refresh_expires_in'] = $resource->refresh_expires_in;
        $data['refresh_token'] = $resource->refresh_token;
        $data['token_type'] = $resource->token_type;
        //$data['not-before-policy'] = $resource->not-before-policy;
        $data['session_state'] = $resource->session_state;
        $data['scope'] = $resource->scope;

        $result['success'] = true;
        $result['data'] = $data;
        return $result;

    }

    public function keycloakLogout(string $token) : array
    {

        $uri = sprintf(self::API_KEYCLOAK_LOGOUT, $this->keycloakBaseUrl, $this->keycloakRealm);
        $headers = [
            'Content-Type' => 'application/x-www-form-urlencoded',
            'Authorization' => 'Bearer ' . $token
        ];

        $form = [];
        $form['client_id'] = $this->keycloakClientId;
        $form['client_secret'] = $this->keycloakClientSecret;
        $form['token'] = $token;

        $response = HttpFacade::post($uri, $headers, $form);
        $resource = json_decode($response->getBody());

        if (isset($resource->error)) {
            $result['success'] = false;
            $result['data']['error'] = $resource->error;
            $result['data']['error_description'] = $resource->error_description;
            return $result;
        }

        $data = [];
        $data['revoke'] = $resource;

        $result['success'] = true;
        $result['data'] = $data;

        return $result;
    }

    public function keycloakLoggedUser(string $token) : array
    {
        $uri = sprintf(self::API_KEYCLOAK_LOGGED_USER_SESSION, $this->keycloakBaseUrl, $this->keycloakRealm);
        $headers = [
            'Content-Type' => 'application/x-www-form-urlencoded',
            'Authorization' => 'Bearer ' . $token
        ];

        $form = [];
        $form['client_id'] = $this->keycloakClientId;
        $form['client_secret'] = $this->keycloakClientSecret;
        $form['token'] = $token;

        $response = HttpFacade::post($uri, $headers, $form);
        $resource = json_decode($response->getBody());

        if (!isset($resource->active)) {
            $result = [];
            $result['success'] = false;
            $result['detail'] = "Error during introspect request, authorization token passed is null";
            $result['data'] = $resource;
            return $result;
        }

        $uriInfo = sprintf(self::API_KEYCLOAK_LOGGED_USER_INFO, $this->keycloakBaseUrl, $this->keycloakRealm);

        $userInfoResponse = HttpFacade::get($uriInfo, $headers);
        $userInfoResource = json_decode($userInfoResponse->getBody());

        if (isset($userInfoResource->error)) {
            $result = [];
            $result['success'] = false;
            $result['data'] = $userInfoResource;
            return $result;
        }

        $data = [];
        $data['introspect'] = $resource;
        $data['info'] = $userInfoResource;

        $result['success'] = true;
        $result['data'] = $data;

        return $result;

    }


    public function keycloakListUsers(string $token) : array
    {

        $uri = sprintf(self::API_KEYCLOAK_LIST_USERS, $this->keycloakBaseUrl, $this->keycloakRealm);
        $headers = [
            'Content-Type' => 'application/x-www-form-urlencoded',
            'Authorization' => 'Bearer ' . $token
        ];

        $response = HttpFacade::get($uri, $headers);
        $resource = json_decode($response->getBody(), true);

        if (isset($resource['error'])) {
            $result = [];
            $result['success'] = false;
            $result['code'] = $response->getStatusCode();
            $result['data'] = $resource;
            return $result;
        }

        $data = [];
        $data['users'] = $resource;

        $result['success'] = true;
        $result['code'] = $response->getStatusCode();
        $result['data'] = $data;

        return $result;
    }

    public function keycloakGetUserByUuid(string $uuid, string $token) : array
    {

        $uri = sprintf(self::API_KEYCLOAK_GET_USER_BY_ID, $this->keycloakBaseUrl, $this->keycloakRealm, $uuid);
        $headers = [
            'Content-Type' => 'application/x-www-form-urlencoded',
            'Authorization' => 'Bearer ' . $token
        ];

        $response = HttpFacade::get($uri, $headers);
        $resource = json_decode($response->getBody(), true);

        if (isset($resource['error'])) {
            $result = [];
            $result['success'] = false;
            $result['code'] = $response->getStatusCode();
            $result['data'] = $resource;
            return $result;
        }

        $data = [];
        $data['user'] = $resource;

        $result['success'] = true;
        $result['code'] = $response->getStatusCode();
        $result['data'] = $data;

        return $result;
    }


    public function keycloakGetUserByUsername(string $username, string $token) : array
    {

        $uri = sprintf(self::API_KEYCLOAK_LIST_USERS, $this->keycloakBaseUrl, $this->keycloakRealm);
        $headers = [
            'Content-Type' => 'application/x-www-form-urlencoded',
            'Authorization' => 'Bearer ' . $token
        ];

        $response = HttpFacade::get($uri, $headers);
        $resource = json_decode($response->getBody(), true);

        if (isset($resource['error'])) {
            $result = [];
            $result['success'] = false;
            $result['code'] = $response->getStatusCode();
            $result['data'] = $resource;
            return $result;
        }


        //TODO: chiamare this->search, fare get first del risultato della search, prendere uuid dell'utente trovato, fare this->getUserByUuid e comparare l'username trovato con quello passato come paramentro.

        $data = [];
        $data['user'] = '';

        foreach ($resource as $user) {
            if ($user['username'] == $username) {
                $data['user'] = $user;
            }
        }


        if ($data['user'] == '') {
            $result['success'] = false;
            $result['code'] = $response->getStatusCode();
            $result['data'] = "User not found";
            return $result;
        }

        $result['success'] = true;
        $result['code'] = $response->getStatusCode();
        $result['data'] = $data;

        return $result;
    }



    public function keycloakCreateUser(array $payload, string $token) : array
    {

        $uri = sprintf(self::API_KEYCLOAK_CREATE_USER, $this->keycloakBaseUrl, $this->keycloakRealm);
        $headers = [
            'Content-Type' => 'application/json',
            'Authorization' => 'Bearer ' . $token
        ];

        $response = HttpFacade::postRaw($uri, $headers, json_encode($payload));

        if ($response->getStatusCode() === 201) {
            $data = [];
            $data['user'] = $payload;

            $result['success'] = true;
            $result['code'] = $response->getStatusCode();
            $result['message'] = "User created";
            $result['data'] = $data;

            return $result;
        }

        if ($response->getStatusCode() === 409) {
            $data = [];
            $data['user'] = $payload;

            $result['success'] = false;
            $result['code'] = $response->getStatusCode();
            $result['message'] = "User already exists";
            $result['data'] = $data;

            return $result;
        }

        $data = [];
        $data['user'] = $payload;

        $result['success'] = false;
        $result['code'] = $response->getStatusCode();
        $result['message'] = "Error while creating user";
        $result['data'] = $data;

        return $result;
    }

    public function keycloakEditUser(string $uuid, array $payload, string $token) : array
    {

        $uri = sprintf(self::API_KEYCLOAK_EDIT_USER, $this->keycloakBaseUrl, $this->keycloakRealm, $uuid);
        $headers = [
            'Content-Type' => 'application/json',
            'Authorization' => 'Bearer ' . $token
        ];

        $response = HttpFacade::putRaw($uri, $headers, json_encode($payload));

        if ($response->getStatusCode() === 204) {
            $data = [];
            $data['user'] = $payload;

            $result['success'] = true;
            $result['code'] = $response->getStatusCode();
            $result['message'] = "User edited";
            $result['data'] = $data;

            return $result;
        }

        if ($response->getStatusCode() === 404) {
            $data = [];
            $data['user'] = $payload;

            $result['success'] = false;
            $result['code'] = $response->getStatusCode();
            $result['message'] = "User not found";
            $result['data'] = $data;

            return $result;
        }

        $data = [];
        $data['user'] = $payload;

        $result['success'] = false;
        $result['code'] = $response->getStatusCode();
        $result['message'] = "Error while editing user";
        $result['data'] = $data;

        return $result;
    }

    public function keycloakDeleteUser(string $uuid, string $token) : array
    {

        $uri = sprintf(self::API_KEYCLOAK_DELETE_USER, $this->keycloakBaseUrl, $this->keycloakRealm, $uuid);
        $headers = [
            'Content-Type' => 'application/x-www-form-urlencoded',
            'Authorization' => 'Bearer ' . $token
        ];

        $response = HttpFacade::delete($uri, $headers);

        if ($response->getStatusCode() === 204) {
            $data = [];
            $data['user'] = $uuid;

            $result['success'] = true;
            $result['code'] = $response->getStatusCode();
            $result['message'] = "User deleted";
            $result['data'] = $data;

            return $result;
        }

        if ($response->getStatusCode() === 404) {
            $data = [];
            $data['user'] = $uuid;

            $result['success'] = false;
            $result['code'] = $response->getStatusCode();
            $result['message'] = "User not found";
            $result['data'] = $data;

            return $result;
        }

        $data = [];
        $data['user'] = $uuid;

        $result['success'] = false;
        $result['code'] = $response->getStatusCode();
        $result['message'] = "Error while deleting user";
        $result['data'] = $data;

        return $result;
    }

    public function keycloakSearchUsers(string $query, string $token) : array
    {

        $uri = sprintf(self::API_KEYCLOAK_SEARCH_USERS, $this->keycloakBaseUrl, $this->keycloakRealm, $query);
        $headers = [
            'Content-Type' => 'application/x-www-form-urlencoded',
            'Authorization' => 'Bearer ' . $token
        ];

        $response = HttpFacade::get($uri, $headers);
        $resource = json_decode($response->getBody(), true);

        if (isset($resource['error'])) {
            $result = [];
            $result['success'] = false;
            $result['code'] = $response->getStatusCode();
            $result['data'] = $resource;
            return $result;
        }

        $data = [];
        $data['users'] = $resource;

        $result['success'] = true;
        $result['code'] = $response->getStatusCode();
        $result['data'] = $data;

        return $result;
    }

}