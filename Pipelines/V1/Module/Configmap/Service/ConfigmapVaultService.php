<?php
namespace Pipelines\Configmap\Service;

use Pipelines\Configmap\Helper\SystemEnvHelper;
use Pipelines\Configmap\Helper\HttpClientHelper;

class ConfigmapVaultService
{
    private const API_RUNS_URL = 'https://dev-vault-xpipe.sindria.org/v1/%s/metadata?list=true';
//    private const API_REPOS_URL = 'https://api.github.com/orgs/%s/repos';
//    private const API_STOP_RUN_URL = 'https://api.github.com/repos/%s/%s/actions/runs/%s/cancel';


    private HttpClientHelper $httpClientHelper;

    private string $token;

    public function __construct(HttpClientHelper $httpClientHelper)
    {
        $this->httpClientHelper = $httpClientHelper;
        $this->token = SystemEnvHelper::get('PIPELINES_CONFIGMAP_VAULT_ACCESS_TOKEN');

    }

    public function listConfigmaps($owner)
    {
        $uri = sprintf(self::API_RUNS_URL, $owner);
        $headers = [
            'Content-Type' => 'application/json',
            "X-Vault-Token" => $this->token,
        ];
//        dd($uri);

        $response = $this->httpClientHelper->get($uri, $headers);

        $resource = json_decode($response->getBody(), true);

        return $resource['data']['keys'];

    }



}
