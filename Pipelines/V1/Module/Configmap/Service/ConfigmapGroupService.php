<?php
namespace Pipelines\Configmap\Service;

use Core\Logger\Facade\LoggerFacade;
use Core\MicroFramework\Service\VaultService;
use Pipelines\Configmap\Helper\ConfigmapHelper;
use Core\Http\Facade\HttpFacade;

class ConfigmapGroupService
{
    private const API_IAM_GROUPS_GET_ATTACHED_GROUPS_URL = '%s/rest/V1/iam/groups/attached/groups/%s';


    protected string $iamCollectorBaseUrl;
    protected string $getIamUserAccessToken;


    public function __construct()
    {
        $this->iamCollectorBaseUrl = ConfigmapHelper::getIamCollectorBaseUrl();
        $this->getIamUserAccessToken = ConfigmapHelper::getIamUserAccessToken();
    }


    public function getAttachedGroups(string $owner)
    {
        $uri = sprintf(self::API_IAM_GROUPS_GET_ATTACHED_GROUPS_URL, $this->iamCollectorBaseUrl, $owner);
        $headers = [
            'Content-Type' => 'application/json',
            "X-Token-XPipe" => $this->getIamUserAccessToken,
        ];

        $response = HttpFacade::get($uri, $headers);

        if ($response->getStatusCode() === 404) {
            return [];
        }

        $resource = json_decode($response->getBody(), true);
        return $resource['data']['groups'];

    }

}
