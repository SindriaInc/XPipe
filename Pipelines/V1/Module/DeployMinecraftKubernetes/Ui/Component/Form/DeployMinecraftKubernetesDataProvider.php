<?php
namespace Pipelines\DeployMinecraftKubernetes\Ui\Component\Form;

use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\Data\Collection\EntityFactoryInterface;
use Magento\Ui\DataProvider\AbstractDataProvider;
use Pipelines\DeployMinecraftKubernetes\Model\DeployMinecraftKubernetes;
use Pipelines\DeployMinecraftKubernetes\Model\Form\DeployMinecraftKubernetesCollection;


class DeployMinecraftKubernetesDataProvider extends AbstractDataProvider
{

    protected $collection;

    /**
     * @var array
     */
    public $loadedData;


    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,

        EntityFactoryInterface $entityFactory,
        array $meta = [],
        array $data = []
    ) {
        LoggerFacade::debug('DeployMinecraftKubernetesDataProvider::__construct', [
            'name' => $name,
            'primaryFieldName' => $primaryFieldName,
            'requestFieldName' => $requestFieldName
        ]);

        $form = \Pipelines\DeployMinecraftKubernetes\Model\DeployMinecraftKubernetes::getInstance();
        $form('Demo', 'sindria-mc');
//        dd($form);


        $this->collection = new DeployMinecraftKubernetesCollection($entityFactory, $form);

        parent::__construct($name, $primaryFieldName, $requestFieldName, $meta, $data);
    }

    public function getData() : array
    {
        $entry = [];

        $entry = $this->collection->getFirstItem();

        $this->loadedData[null] = $entry->getData();

        return $this->loadedData;
    }
}
