<?php
namespace Pipe\PostLoginSetup\Service;

use Core\Http\Facade\HttpFacade;
use Pipe\PostLoginSetup\Helper\PostLoginSetupHelper;

class PostLoginSetupIamService
{

    private const API_IAM_GROUPS_DEFAULT_ATTACH_URL = '%s/rest/V1/iam/groups/default/attach/%s';
    protected string $iamCollectorBaseUrl;
    protected string $getIamUserAccessToken;


    public function __construct()
    {
        $this->iamCollectorBaseUrl = PostLoginSetupHelper::getIamCollectorBaseUrl();
        $this->getIamUserAccessToken = PostLoginSetupHelper::getIamUserAccessToken();
    }


    public function attachUserToDefaultGroups(string $username)
    {
        $uri = sprintf(self::API_IAM_GROUPS_DEFAULT_ATTACH_URL, $this->iamCollectorBaseUrl, $username);
        $headers = [
            'Content-Type' => 'application/json',
            "X-Token-XPipe" => $this->getIamUserAccessToken,
        ];

        $response = HttpFacade::get($uri, $headers);

        if ($response->getStatusCode() === 404) {
            return [];
        }

        return json_decode($response->getBody(), true);

    }

}
