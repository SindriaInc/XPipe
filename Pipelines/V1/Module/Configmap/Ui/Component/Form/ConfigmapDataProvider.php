<?php
namespace Pipelines\Configmap\Ui\Component\Form;

use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\App\ObjectManager;
use Magento\Framework\Data\Collection\EntityFactoryInterface;
use Magento\Ui\DataProvider\AbstractDataProvider;
use Pipelines\Configmap\Helper\ConfigmapHelper;
use Pipelines\Configmap\Model\Form\ConfigmapCollection;
use Pipelines\Configmap\Service\ConfigmapVaultService;


class ConfigmapDataProvider extends AbstractDataProvider
{

    protected $collection;

    /**
     * @var array
     */
    public $loadedData;

    private string $configmapId;

    private string $owner;

    private ConfigmapVaultService $vaultService;


    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,
        EntityFactoryInterface $entityFactory,
        ConfigmapVaultService  $vaultService,
        array $meta = [],
        array $data = []
    ) {
        LoggerFacade::debug('ConfigmapDataProvider::__construct', [
            'name' => $name,
            'primaryFieldName' => $primaryFieldName,
            'requestFieldName' => $requestFieldName
        ]);

        $this->vaultService = $vaultService;


        // Recupera session in modo statico da ObjectManager
        $objectManager = ObjectManager::getInstance();
        $session = $objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class);

         // If session key does not exist, return null as magento expected to render form default parameters without id.
         // It is not a bug, it's a feature.
        $this->configmapId = $session->getData('configmap_id');
        LoggerFacade::debug('ConfigmapDataProvider::configmap_id from session', [
            'configmap_id' => $this->configmapId
        ]);

        // If session key does not exist, return null as magento expected to render form default parameters without id.
        // It is not a bug, it's a feature.
        $this->owner = $session->getData('owner') ?? 'new-owner';
        LoggerFacade::debug('ConfigmapDataProvider::owner from session', [
            'owner' => $this->owner
        ]);

        $form = \Pipelines\Configmap\Model\Configmap::getInstance();

        if ($this->configmapId !== 'new-configmap') {
            $secrets = $this->vaultService->getKvSecret($this->owner, $this->configmapId)['data'];

            $form(
                $this->configmapId,
                $this->owner,
                ConfigmapHelper::makeLabelFromSlug($this->configmapId),
                $secrets['AWS_ACCESS_KEY_ID'],
                $secrets['AWS_SECRET_ACCESS_KEY'],
                $secrets['AWS_DEFAULT_REGION'],
                $secrets['EKS_CLUSTER_NAME'],
                $secrets['AZURE_SUBSCRIPTION_ID'],
                $secrets['AZURE_CLIENT_ID'],
                $secrets['AZURE_SECRET'],
                $secrets['AZURE_TENANT'],
                $secrets['AZURE_RESOURCE_GROUP'],
                $secrets['AZURE_STORAGE_ACCOUNT'],
                $secrets['AZURE_STORAGE_ACCESS_KEY'],
                $secrets['AZURE_STORAGE_CONNECTION_STRING'],
                $secrets['AZURE_INI'],
                $secrets['AZURE_CONF'],
                $secrets['DOCKERHUB_USERNAME'],
                $secrets['DOCKERHUB_PASSWORD'],
                $secrets['DOCKERHUB_NAMESPACE'],
                $secrets['DOCKERHUB_PRIVATE_NAMESPACE'],
                $secrets['SCM_GIT_PROTOCOL'],
                $secrets['SCM_GIT_FQDN'],
                $secrets['SCM_GIT_NAMESPACE'],
                $secrets['SCM_GIT_USERNAME'],
                $secrets['SCM_GIT_PASSWORD'],
                $secrets['SCM_GIT_ACCESS_TOKEN'],
                $secrets['CRT_CERTBOT_CACHE'],
                $secrets['CRT_CERTBOT_DOMAIN'],
                $secrets['CRT_CERTBOT_EMAIL'],
                $secrets['SSH_HOST'],
                $secrets['SSH_PORT'],
                $secrets['SSH_USER'],
                $secrets['SSH_PASSWORD'],
                $secrets['SSH_PRIVATE_KEY'],
                $secrets['RKE2_CLUSTER_NAME'],
                $secrets['RKE2_KUBECONFIG'],
                $secrets['IAC_INVENTORY_CACHE'],
                $secrets['IAC_INVENTORY_NAME'],
                $secrets['IAC_INVENTORY_REMOTE'],
            );
        } else {
            $form(
                $this->configmapId,
                $this->owner,
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                '',
            );
        }


        $this->collection = new ConfigmapCollection($entityFactory, $form);

        parent::__construct($name, $primaryFieldName, $requestFieldName, $meta, $data);
    }

    public function getData() : array
    {
        $entry = [];
        $entry = $this->collection->getFirstItem();
        $this->loadedData[$this->configmapId] = $entry->getData();

        return $this->loadedData;
    }
}
