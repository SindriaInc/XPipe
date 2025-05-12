<?php

namespace Sindria\OAuth2\Client\Provider;

use Firebase\JWT\JWT;
use League\OAuth2\Client\Provider\ResourceOwnerInterface;

/**
 * KeycloakResourceOwner
 */
class KeycloakResourceOwner implements ResourceOwnerInterface
{
    /**
     * Raw response
     *
     * @var array
     */
    protected $response;

    protected $tokenInfo;

    /**
     * Creates new resource owner.
     *
     * @param array  $response
     */
    public function __construct(array $response = array(), $token = null)
    {
        $this->response = $response;

        $tokenInfo = $this->parseToken($token);

        $this->tokenInfo = empty($tokenInfo) ? $response : $tokenInfo;
    }

    /**
     * parseToken 
     *
     * @param $accessToken
     *
     * @return 
     */
    protected function parseToken($accessToken = null) 
    {
        if (empty($accessToken)) {
            return [];
        }
        $accessTokenArr = explode('.', $accessToken);
        if (count($accessTokenArr) != 3) {
            return [];
        }

        return @json_decode(JWT::urlsafeB64Decode($accessTokenArr[1]), true);
    }

    /**
     * Get resource owner id
     *
     * @return string|null
     */
    public function getId()
    {
        return $this->response['sub'] ?: null;
    }

    /**
     * Get resource owner email
     *
     * @return string|null
     */
    public function getEmail()
    {
        return $this->response['email'] ?: null;
    }

    /**
     * Get resource owner name
     *
     * @return string|null
     */
    public function getName()
    {
        return $this->response['username'] ?: null;
    }

    /**
     * Return all of the owner details available as an array.
     *
     * @return array
     */
    public function toArray()
    {
        return $this->response;
    }

    /**
     * getAttr 
     *
     * @param $key string
     * @param $type string
     *
     * @return 
     */
    public function getAttr($key = '', $type = 'response')
    {
        if (empty($key)) {
            return null;
        }
        return isset($this->$type[$key]) ? $this->$type[$key] : null;
    }

    /**
     * getCurrentClient 
     *
     * @return 
     */
    public function getCurrentClientId() {
        return $this->getAttr('azp', 'tokenInfo');
    }

    /**
     * getClients 
     *
     * @return array
     */
    public function getClients()
    {
        return array_keys($this->getAttr('resource_access', 'tokenInfo'));
    }

    /**
     * getResources 
     *
     * @return 
     */
    public function getResources()
    {
        $permissionList = $this->getPermissions();
        return array_column((array)$permissionList, 'rsid');
    }

    /**
     * getRoles 
     *
     * @param $clientId string
     *
     * @return 
     */
    public function getRoles($clientId = null)
    {
        if (empty($clientId)) {
            $clientId = $this->getCurrentClientId();
        }
        $resourceList = $this->getAttr('resource_access', 'tokenInfo');
        if (isset($resourceList[$clientId]) && isset($resourceList[$clientId]['roles'])) {
            return $resourceList[$clientId]['roles'];
        }

        return [];
    }

    /**
     * getPermissions 
     *
     * @return 
     */
    public function getPermissions()
    {
        $authorization = $this->getAttr('authorization', 'tokenInfo');
        return isset($authorization['permissions']) ? $authorization['permissions'] : [];
    }

    /**
     * can do with permission
     *
     * @param $permission
     *
     * @return 
     */
    public function can($resource, $scope = null)
    {
        $permissionList = $this->getPermissions();
        foreach((array)$permissionList as $permission) {
            if (!isset($permission['rsname'])) {
                continue;
            }

            if (!empty($permission['scopes']) && (empty($scope) || !in_array($scope, $permission['scopes']))) {
                continue;
            }

            if ($permission['rsname'] == $resource) {
                return true;
            }
        }

        return false;
    }

    /**
     * cannot do with permission
     *
     * @param $permission
     *
     * @return 
     */
    public function cannot($resource, $scope = null)
    {
        return !$this->can($resource, $scope);
    }

    /**
     * hasRole 
     *
     * @param $role string
     * @param $clientId string
     *
     * @return 
     */
    public function hasRole($role, $clientId = null)
    {
        if (empty($clientId)) {
            $clientId = $this->getCurrentClientId();
        }

        return in_array($role, $this->getRoles($clientId));
    }

    /**
     * inRoles 
     *
     * @param $rolesList array
     * @param $clientId string
     *
     * @return 
     */
    public function inRoles($rolesList, $clientId = null)
    {
        if (empty($clientId)) {
            $clientId = $this->getCurrentClientId();
        }
        $clientRoleList = $this->getRoles($clientId);

        return !empty(array_intersect($rolesList, $clientRoleList));
    }
}
