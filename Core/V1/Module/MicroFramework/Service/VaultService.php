<?php

namespace Core\MicroFramework\Service;

use Core\Http\Facade\HttpFacade;

abstract class VaultService
{

    protected string $vaultBaseUrl;
    protected string $vaultAccessToken;



    private const API_VAULT_LIST_SECRETS_IN_MOUNT_URL = '%s/v1/%s/metadata?list=true';
    private const API_VAULT_GET_KV_SECRET_URL = '%s/v1/%s/data/%s';
    private const API_VAULT_DELETE_KV_SECRET_URL = '%s/v1/%s/metadata/%s';
    private const API_VAULT_GET_MOUNT_URL = '%s/v1/sys/internal/ui/mounts/%s';
    private const API_VAULT_LIST_MOUNTS_URL = '%s/v1/sys/mounts';
    private const API_VAULT_ENABLE_MOUNTS_URL = '%s/v1/sys/mounts/%s';
    private const API_VAULT_DISABLE_MOUNTS_URL = '%s/v1/sys/mounts/%s';


    public function __construct(string $vaultBaseUrl, string $vaultAccessToken)
    {
        $this->vaultBaseUrl = $vaultBaseUrl;
        $this->vaultAccessToken = $vaultAccessToken;


    }


    /**
     * @param string $mount
     * @return mixed
     */
    public function listSecretsInMount(string $mount)
    {
        $uri = sprintf(self::API_VAULT_LIST_SECRETS_IN_MOUNT_URL, $this->vaultBaseUrl, $mount);
        $headers = [
            'Content-Type' => 'application/json',
            "X-Vault-Token" => $this->vaultAccessToken,
        ];

        $response = HttpFacade::get($uri, $headers);

        if ($response->getStatusCode() === 404) {
            return [];
        }

        $resource = json_decode($response->getBody(), true);
        return $resource['data']['keys'];
    }


    /**
     * @param string $mount
     * @param string $secretId
     * @return array
     */
    public function getKvSecret(string $mount, string $secretId) : array
    {
        $uri = sprintf(self::API_VAULT_GET_KV_SECRET_URL, $this->vaultBaseUrl, $mount, $secretId);
        $headers = [
            'Content-Type' => 'application/json',
            "X-Vault-Token" => $this->vaultAccessToken,
        ];

        $response = HttpFacade::get($uri, $headers);
        $resource = json_decode($response->getBody(), true);
        return $resource['data']['data'];
    }


    public function mountExists(string $mount) : bool
    {
        $uri = sprintf(self::API_VAULT_GET_MOUNT_URL, $this->vaultBaseUrl, $mount);

        $headers = [
            'Content-Type' => 'application/json',
            "X-Vault-Token" => $this->vaultAccessToken,
        ];

        $response = HttpFacade::get($uri, $headers);

        if ($response->getStatusCode() === 200) {
            return true;
        }

        return false;

    }


    public function listMounts()
    {

        $uri = sprintf(self::API_VAULT_LIST_MOUNTS_URL, $this->vaultBaseUrl);
        $headers = [
            'Content-Type' => 'application/json',
            "X-Vault-Token" => $this->vaultAccessToken,
        ];

        $response = HttpFacade::get($uri, $headers);

        if ($response->getStatusCode() === 404) {
            return [];
        }

        return json_decode($response->getBody(), true);
    }

    public function enableKvMount(
        string $mount,
        string $description,
        array $config = [
            'default_lease_ttl' => 0,
            'force_no_cache' => false,
            'listing_visibility' => 'hidden',
            'max_lease_ttl' => 0
        ]
    ) : array
    {
        $uri = sprintf(self::API_VAULT_ENABLE_MOUNTS_URL, $this->vaultBaseUrl, $mount);

        $headers = [
            'Content-Type' => 'application/json',
            "X-Vault-Token" => $this->vaultAccessToken,
        ];

        $payload = [
            'type' => 'kv',
            'description' => $description,
            'config' => $config,
            'options' => [
                'version' => '2',
            ]
        ];

        $response = HttpFacade::postRaw($uri, $headers, json_encode($payload));

        if ($response->getStatusCode() === 400) {
            return json_decode($response->getBody(), true);
        }

        return [];

    }

    public function disableKvMount(string $mount) : array
    {

        $uri = sprintf(self::API_VAULT_ENABLE_MOUNTS_URL, $this->vaultBaseUrl, $mount);

        $headers = [
            'Content-Type' => 'application/json',
            "X-Vault-Token" => $this->vaultAccessToken,
        ];

        $response = HttpFacade::delete($uri, $headers);

        if ($response->getStatusCode() === 204) {
            return [];
        }

        return [];

    }

}