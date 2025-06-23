<?php
namespace Pipelines\Configmap\Service;

use Core\Logger\Facade\LoggerFacade;
use Core\MicroFramework\Service\VaultService;
use Pipelines\Configmap\Helper\ConfigmapHelper;
use Core\Http\Facade\HttpFacade;

class ConfigmapVaultService extends VaultService
{
    private const API_CONFIGMAP_SECRETS_URL = '%s/v1/%s/data/%s';
    private const API_CONFIGMAP_SECRETS_DELETE_URL = '%s/v1/%s/metadata/%s';

    public function __construct()
    {
        parent::__construct(
            ConfigmapHelper::getPipelinesConfigmapVaultBaseUrl(),
            ConfigmapHelper::getPipelinesConfigmapVaultAccessToken()
        );
    }

    /**
     * @param string $owner
     * @return mixed
     */
    public function listConfigmaps(string $owner)
    {
      return $this->listSecretsInMount($owner);
    }

    public function saveSecret($data) : array
    {

        if ($data['configmap_id'] === "new-configmap") {
            $data['configmap_id'] = ConfigmapHelper::makeSlugFromLabel($data['configmap_name']);

            $uri = sprintf(self::API_CONFIGMAP_SECRETS_URL, $this->vaultBaseUrl, $data['owner'], $data['configmap_id']);

            $headers = [
                'Content-Type' => 'application/json',
                "X-Vault-Token" => $this->vaultAccessToken,
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

            $uri = sprintf(self::API_CONFIGMAP_SECRETS_URL, $this->vaultBaseUrl, $data['owner'], $data['configmap_id']);

            $headers = [
                'Content-Type' => 'application/json',
                "X-Vault-Token" => $this->vaultAccessToken,
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

        $uri = sprintf(self::API_CONFIGMAP_SECRETS_DELETE_URL, $this->vaultBaseUrl, $owner, $configmapId);
        $headers = [
            'Content-Type' => 'application/json',
            "X-Vault-Token" => $this->vaultAccessToken,
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
