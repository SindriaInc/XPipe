<?php
namespace Pipelines\PipeManager\Service;

use Pipelines\PipeManager\Helper\SystemEnvHelper;
use Pipelines\PipeManager\Helper\HttpClientHelper;

class GithubActionsService
{
    const API_RUNS_URL = 'https://api.github.com/repos/%s/%s/actions/runs';
    const API_REPOS_URL = 'https://api.github.com/orgs/%s/repos';

//    const REPO = 'demo-dev-dorje';

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

        $resource = json_decode($response, true);

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
        $resource = json_decode($response, true);

        return $resource;
    }

}
