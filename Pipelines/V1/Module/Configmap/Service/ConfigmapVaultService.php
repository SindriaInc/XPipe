<?php
namespace Pipelines\Configmap\Service;

use Pipelines\Configmap\Helper\SystemEnvHelper;
use Pipelines\Configmap\Helper\HttpClientHelper;

class ConfigmapVaultService
{
    private const API_RUNS_URL = 'https://api.github.com/repos/%s/%s/actions/runs';
    private const API_REPOS_URL = 'https://api.github.com/orgs/%s/repos';
    private const API_STOP_RUN_URL = 'https://api.github.com/repos/%s/%s/actions/runs/%s/cancel';


    private HttpClientHelper $httpClientHelper;

    private string $token;

    public function __construct(HttpClientHelper $httpClientHelper)
    {
        $this->httpClientHelper = $httpClientHelper;
        $this->token = SystemEnvHelper::get('PIPELINES_GITHUB_ACCESS_TOKEN');

    }

    public function listWorkflowRunsForARepository($owner, $repo)
    {
        $uri = sprintf(self::API_RUNS_URL, $owner, $repo);
        $headers = [
            'Content-Type' => 'application/json',
            "X-GitHub-Api-Version" => "2022-11-28",
            "Authorization" => "Bearer " . $this->token,
        ];
//        dd($uri);

        $response = $this->httpClientHelper->get($uri, $headers);

        $resource = json_decode($response->getBody(), true);

        return $resource['workflow_runs'];

    }

    public function listOrganizationRepositories(string $owner)
    {
        $uri = sprintf(self::API_REPOS_URL, $owner);
        $headers = [
            'Content-Type' => 'application/json',
            "X-GitHub-Api-Version" => "2022-11-28",
            "Authorization" => "Bearer " . $this->token,
        ];

        $response = $this->httpClientHelper->get($uri, $headers);
        $resource = json_decode($response->getBody(), true);

        return $resource;
    }

    public function cancelAWorkflowRun(string $owner, string $repo, string $runId)
    {
        $uri = sprintf(self::API_STOP_RUN_URL, $owner, $repo, $runId);

        $headers = [
            'Content-Type' => 'application/json',
            "X-GitHub-Api-Version" => "2022-11-28",
            "Authorization" => "Bearer " . $this->token,
        ];

        $response = $this->httpClientHelper->post($uri, $headers);

        return $response;
    }

}
