<?php
namespace Pipelines\Configmap\Service;

use Pipelines\Configmap\Helper\SystemEnvHelper;
use Pipelines\Configmap\Helper\HttpClientHelper;

class ConfigmapVaultService
{
    private const API_CONFIGMAP_LIST_URL = 'https://dev-vault-xpipe.sindria.org/v1/%s/metadata?list=true';
    private const API_CONFIGMAP_SECRETS_URL = 'https://dev-vault-xpipe.sindria.org/v1/%s/data/%s';

    private HttpClientHelper $httpClientHelper;

    private string $token;

    public function __construct(HttpClientHelper $httpClientHelper)
    {
        $this->httpClientHelper = $httpClientHelper;
        $this->token = SystemEnvHelper::get('PIPELINES_CONFIGMAP_VAULT_ACCESS_TOKEN');

    }

    /**
     * @param string $owner
     * @return mixed
     */
    public function listConfigmaps(string $owner)
    {
        $uri = sprintf(self::API_CONFIGMAP_LIST_URL, $owner);
        $headers = [
            'Content-Type' => 'application/json',
            "X-Vault-Token" => $this->token,
        ];

        $response = $this->httpClientHelper->get($uri, $headers);
        $resource = json_decode($response->getBody(), true);
        return $resource['data']['keys'];
    }

    /**
     * @param string $owner
     * @param string $configmapId
     * @return array
     */
    public function getSecret(string $owner, string $configmapId) : array
    {
        $uri = sprintf(self::API_CONFIGMAP_SECRETS_URL, $owner, $configmapId);
        $headers = [
            'Content-Type' => 'application/json',
            "X-Vault-Token" => $this->token,
        ];

        $response = $this->httpClientHelper->get($uri, $headers);
        $resource = json_decode($response->getBody(), true);
        return $resource['data']['data'];
    }



}
