<?php
namespace Pipelines\Configmap\Service;

use Core\Logger\Facade\LoggerFacade;
use Pipelines\Configmap\Helper\ConfigmapHelper;
use Pipelines\Configmap\Helper\SystemEnvHelper;
use Core\Http\Facade\HttpFacade;

class ConfigmapVaultService
{
    // TODO: IMPORTANT check for empty and invalid token

    private const API_CONFIGMAP_LIST_URL = 'https://dev-vault-xpipe.sindria.org/v1/%s/metadata?list=true';
    private const API_CONFIGMAP_SECRETS_URL = 'https://dev-vault-xpipe.sindria.org/v1/%s/data/%s';
    private const API_CONFIGMAP_SECRETS_DELETE_URL = 'https://dev-vault-xpipe.sindria.org/v1/%s/metadata/%s';



    private string $token;

    public function __construct()
    {
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

        $response = HttpFacade::get($uri, $headers);
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

        $response = HttpFacade::get($uri, $headers);
        $resource = json_decode($response->getBody(), true);
        return $resource['data']['data'];
    }

    public function saveSecret($data) : array
    {

        if ($data['configmap_id'] === "new-configmap") {
            $data['configmap_id'] = ConfigmapHelper::makeSlugFromLabel($data['configmap_name']);

            $uri = sprintf(self::API_CONFIGMAP_SECRETS_URL, $data['owner'], $data['configmap_id']);

            $headers = [
                'Content-Type' => 'application/json',
                "X-Vault-Token" => $this->token,
            ];


            $payload = json_encode(ConfigmapHelper::preparePayload($data));

            try {
                $response = HttpFacade::postRaw($uri, $headers, $payload);

                if ($response->getStatusCode() !== 200) {

                    LoggerFacade::error('ConfigmapVaultService::saveSecret failed api call', [
                        'status_code' => $response->getStatusCode(),
                        'body' => $response->getBody(),
                    ]);

                    return ['success' => false];
                }

                $resource = json_decode($response->getBody(), true);

                LoggerFacade::info('ConfigmapVaultService::saveSecret secret added successfully', [
                    'status_code' => $response->getStatusCode(),
                    'body' => $response->getBody(),
                ]);


                $result['success'] = true;
                $result['data'] = $resource;
                $result['configmap_id'] = $data['configmap_id'];
                $result['configmap_name'] = $data['configmap_name'];

                return $result;

            } catch (\Exception $e) {

                LoggerFacade::error('ConfigmapVaultService::saveSecret failed api call exception', [
                    'exception' => $e->getMessage(),
                ]);

                return ['success' => false, 'message' => $e->getMessage()];
            }



        } else {

            $uri = sprintf(self::API_CONFIGMAP_SECRETS_URL, $data['owner'], $data['configmap_id']);

            $headers = [
                'Content-Type' => 'application/json',
                "X-Vault-Token" => $this->token,
            ];


            $payload = json_encode(ConfigmapHelper::preparePayload($data));

            try {
                $response = HttpFacade::putRaw($uri, $headers, $payload);

                if ($response->getStatusCode() !== 200) {

                    LoggerFacade::error('ConfigmapVaultService::saveSecret failed api call', [
                        'status_code' => $response->getStatusCode(),
                        'body' => $response->getBody(),
                    ]);

                    return ['success' => false];
                }



                $resource = json_decode($response->getBody(), true);

                LoggerFacade::info('ConfigmapVaultService::saveSecret secret edited successfully', [
                    'status_code' => $response->getStatusCode(),
                    'body' => $response->getBody(),
                ]);


                $result['success'] = true;
                $result['data'] = $resource;
                $result['configmap_id'] = $data['configmap_id'];
                $result['configmap_name'] = $data['configmap_name'];

                return $result;

            } catch (\Exception $e) {

                LoggerFacade::error('ConfigmapVaultService::saveSecret failed api call exception', [
                    'exception' => $e->getMessage(),
                ]);

                return ['success' => false, 'message' => $e->getMessage()];
            }

        }

    }



    public function deleteSecret(string $owner, string $configmapId) : array
    {

        $uri = sprintf(self::API_CONFIGMAP_SECRETS_DELETE_URL, $owner, $configmapId);
        $headers = [
            'Content-Type' => 'application/json',
            "X-Vault-Token" => $this->token,
        ];

        try {

            $response = HttpFacade::delete($uri, $headers);

            $result['success'] = false;
            $result['owner'] = $owner;
            $result['configmap_id'] = $configmapId;
            $result['configmap_name'] = ConfigmapHelper::makeLabelFromSlug($configmapId);

            if ($response->getStatusCode() === 204) {

                $result['success'] = true;

                LoggerFacade::info('ConfigmapVaultService::deleteSecret secret deleted successfully', [
                    'status_code' => $response->getStatusCode(),
                    'reason_phrase' => $response->getReasonPhrase(),
                ]);

                return $result;
            }

            LoggerFacade::error('ConfigmapVaultService::deleteSecret failed api call', [
                'status_code' => $response->getStatusCode(),
                'body' => $response->getBody(),
            ]);

            return $result;
        } catch (\Exception $e) {

            LoggerFacade::error('ConfigmapVaultService::deleteSecret failed api call exception', [
                'exception' => $e->getMessage(),
            ]);

            return ['success' => false, 'message' => $e->getMessage()];
        }
    }


}
