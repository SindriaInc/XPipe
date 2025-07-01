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

    public function login(string $username, string $password) : array
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

    public function logout(string $token) : array
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

    public function loggedUser(string $token) : array
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


    public function listUsers(string $token) : array
    {

        $uri = sprintf(self::API_KEYCLOAK_LIST_USERS, $this->keycloakBaseUrl, $this->keycloakRealm);
        $headers = [
            'Content-Type' => 'application/x-www-form-urlencoded',
            'Authorization' => 'Bearer ' . $token
        ];

        $response = HttpFacade::get($uri, $headers);
        $resource = json_decode($response->getBody());

        if (isset($resource->error)) {
            $result = [];
            $result['success'] = false;
            $result['data'] = $resource;
            return $result;
        }

        $data = [];
        $data['users'] = $resource;

        $result['success'] = true;
        $result['data'] = $data;

        return $result;
    }

    public function getUser(string $uuid, string $token) : array
    {

        $uri = sprintf(self::API_KEYCLOAK_LIST_USERS, $this->keycloakBaseUrl, $this->keycloakRealm);
        $headers = [
            'Content-Type' => 'application/x-www-form-urlencoded',
            'Authorization' => 'Bearer ' . $token
        ];

        $response = HttpFacade::get($uri, $headers);
        $resource = json_decode($response->getBody());

        if (isset($resource->error)) {
            $result = [];
            $result['success'] = false;
            $result['data'] = $resource;
            return $result;
        }

        $data = [];
        $data['user'] = '';

        foreach ($resource as $user) {
            if ($user->id == $uuid) {
                $data['user'] = $user;
            }
        }


        if ($data['user'] == '') {
            $result['success'] = false;
            $result['data'] = "User not found";
            return $result;
        }

        $result['success'] = true;
        $result['data'] = $data;

        return $result;
    }

//
//    /**
//     * @param string $mount
//     * @return mixed
//     */
//    public function listSecretsInMount(string $mount)
//    {
//        $uri = sprintf(self::API_VAULT_LIST_SECRETS_IN_MOUNT_URL, $this->keycloakBaseUrl, $mount);
//        $headers = [
//            'Content-Type' => 'application/json',
//            "X-Vault-Token" => $this->vaultAccessToken,
//        ];
//
//        $response = HttpFacade::get($uri, $headers);
//
//        if ($response->getStatusCode() === 404) {
//            return [];
//        }
//
//        $resource = json_decode($response->getBody(), true);
//        return $resource['data']['keys'];
//    }
//
//
//    /**
//     * @param string $mount
//     * @param string $secretId
//     * @return array
//     */
//    public function getKvSecret(string $mount, string $secretId) : array
//    {
//        $uri = sprintf(self::API_VAULT_GET_KV_SECRET_URL, $this->keycloakBaseUrl, $mount, $secretId);
//        $headers = [
//            'Content-Type' => 'application/json',
//            "X-Vault-Token" => $this->vaultAccessToken,
//        ];
//
//        $response = HttpFacade::get($uri, $headers);
//        $resource = json_decode($response->getBody(), true);
//        return $resource['data']['data'];
//    }
//
//
//    public function mountExists(string $mount) : bool
//    {
//        $uri = sprintf(self::API_VAULT_GET_MOUNT_URL, $this->keycloakBaseUrl, $mount);
//
//        $headers = [
//            'Content-Type' => 'application/json',
//            "X-Vault-Token" => $this->vaultAccessToken,
//        ];
//
//        $response = HttpFacade::get($uri, $headers);
//
//        if ($response->getStatusCode() === 200) {
//            return true;
//        }
//
//        return false;
//
//    }
//
//
//    public function listMounts()
//    {
//
//        $uri = sprintf(self::API_VAULT_LIST_MOUNTS_URL, $this->keycloakBaseUrl);
//        $headers = [
//            'Content-Type' => 'application/json',
//            "X-Vault-Token" => $this->vaultAccessToken,
//        ];
//
//        $response = HttpFacade::get($uri, $headers);
//
//        if ($response->getStatusCode() === 404) {
//            return [];
//        }
//
//        return json_decode($response->getBody(), true);
//    }
//
//    public function enableKvMount(
//        string $mount,
//        string $description,
//        array $config = [
//            'default_lease_ttl' => 0,
//            'force_no_cache' => false,
//            'listing_visibility' => 'hidden',
//            'max_lease_ttl' => 0
//        ]
//    ) : array
//    {
//        $uri = sprintf(self::API_VAULT_ENABLE_MOUNTS_URL, $this->keycloakBaseUrl, $mount);
//
//        $headers = [
//            'Content-Type' => 'application/json',
//            "X-Vault-Token" => $this->vaultAccessToken,
//        ];
//
//        $payload = [
//            'type' => 'kv',
//            'description' => $description,
//            'config' => $config,
//            'options' => [
//                'version' => '2',
//            ]
//        ];
//
//        $response = HttpFacade::postRaw($uri, $headers, json_encode($payload));
//
//        if ($response->getStatusCode() === 400) {
//            return json_decode($response->getBody(), true);
//        }
//
//        return [];
//
//    }
//
//    public function disableKvMount(string $mount) : array
//    {
//
//        $uri = sprintf(self::API_VAULT_ENABLE_MOUNTS_URL, $this->keycloakBaseUrl, $mount);
//
//        $headers = [
//            'Content-Type' => 'application/json',
//            "X-Vault-Token" => $this->vaultAccessToken,
//        ];
//
//        $response = HttpFacade::delete($uri, $headers);
//
//        if ($response->getStatusCode() === 204) {
//            return [];
//        }
//
//        return [];
//
//    }

}