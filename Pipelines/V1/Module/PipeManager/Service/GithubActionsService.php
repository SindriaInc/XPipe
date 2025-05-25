<?php
namespace Pipelines\PipeManager\Service;

use Core\Notifications\Helper\SystemEnvHelper;
use Pipelines\PipeManager\Helper\HttpClientHelper;

class GithubActionsService
{
    const API_URL = 'https://api.github.com/repos/%s/%s/actions/runs';
    const OWNER = 'XPipePipelines';
    const REPO = 'demo-dev-dorje';

    private HttpClientHelper $httpClientHelper;

    private string $token;

    public function __construct(HttpClientHelper $httpClientHelper)
    {
        $this->httpClientHelper = $httpClientHelper;
        $this->token = SystemEnvHelper::get('PIPELINES_GITHUB_ACCESS_TOKEN');

    }

    public function listWorkflowRunsForARepository($owner = self::OWNER, $repo = self::REPO)
    {
        $uri = sprintf(self::API_URL, $owner, $repo);
        $headers = [
            'Content-Type' => 'application/json',
            "X-GitHub-Api-Version" => "2022-11-28",
            "Authorization" => "Bearer " . $this->token,
        ];

        return $this->httpClientHelper->get($uri, $headers);

    }

}
