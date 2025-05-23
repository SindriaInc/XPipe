<?php
namespace Pipelines\PipeManager\Service;

class GithubActionsService
{
    const API_URL = 'https://api.github.com/repos/%s/%s/actions/runs';
    const OWNER = 'magento';
    const REPO = 'magento2';

    public function getLatestRuns($owner = self::OWNER, $repo = self::REPO)
    {
        $url = sprintf(self::API_URL, $owner, $repo);
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_USERAGENT, 'Magento PipeManager Bot');
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        $response = curl_exec($ch);
        if ($response === false) {
            curl_close($ch);
            return [];
        }
        curl_close($ch);

        $data = json_decode($response, true);
        return $data['workflow_runs'] ?? [];
    }
}
