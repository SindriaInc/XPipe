<?php
namespace Pipelines\Configmap\Ui\Component\Form;

use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\App\ObjectManager;
use Magento\Framework\Data\Collection\EntityFactoryInterface;
use Magento\Ui\DataProvider\AbstractDataProvider;
use Pipelines\Configmap\Model\Configmap;
use Pipelines\Configmap\Model\Form\ConfigmapCollection;


class ConfigmapDataProvider extends AbstractDataProvider
{

    protected $collection;

    /**
     * @var array
     */
    public $loadedData;

    private $configmapId;


    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,

        EntityFactoryInterface $entityFactory,
        array $meta = [],
        array $data = []
    ) {
        LoggerFacade::debug('ConfigmapDataProvider::__construct', [
            'name' => $name,
            'primaryFieldName' => $primaryFieldName,
            'requestFieldName' => $requestFieldName
        ]);


//         Recupera session in modo statico da ObjectManager
        $objectManager = ObjectManager::getInstance();
        $session = $objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class);
//
         // If session key does not exist, return null as magento expected to render form default parameters without id.
         // It is not a bug, it's a feature.
        $this->configmapId = $session->getData('configmap_id');
        LoggerFacade::debug('ConfigmapDataProvider::configmap_id from session', [
            'configmap_id' => $this->configmapId
        ]);

        $form = \Pipelines\Configmap\Model\Configmap::getInstance();



        $form(
            1,
            'mario.rossi',

        );


        $this->collection = new ConfigmapCollection($entityFactory, $form);
//        dd($this->collection);

        parent::__construct($name, $primaryFieldName, $requestFieldName, $meta, $data);
    }

    public function getData() : array
    {
        $entry = [];

        $entry = $this->collection->getFirstItem();

        $this->loadedData[$this->configmapId] = $entry->getData();

//        dd($this->loadedData);

        return $this->loadedData;
    }
}
