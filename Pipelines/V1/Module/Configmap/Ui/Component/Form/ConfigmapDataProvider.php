<?php
namespace Pipelines\Configmap\Ui\Component\Form;

use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\App\ObjectManager;
use Magento\Framework\Data\Collection\EntityFactoryInterface;
use Magento\Ui\DataProvider\AbstractDataProvider;
use Pipelines\Configmap\Helper\ConfigmapHelper;
use Pipelines\Configmap\Model\Configmap;
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
            $secrets = $this->vaultService->getSecret($this->owner, $this->configmapId);
            //dd($secrets);
            $form(
                $this->configmapId,
                $this->owner,
                ConfigmapHelper::makeLabelFromSlug($this->configmapId),
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
