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
    protected $loadedData;


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

        $this->collection = new DeployMinecraftKubernetesCollection(
            $entityFactory,
            new DeployMinecraftKubernetes('Demo', 'sindria-mc')
        );

        parent::__construct($name, $primaryFieldName, $requestFieldName, $meta, $data);
    }

    public function getData() : array
    {

        //dd($this->collection);
        //dd($this->collection->getFirstItem());
        //dd($this->collection->getFirstItem()->getData());



        $entry = [];

        //$entry['1']['data'] = $this->collection->getFirstItem()->getData();

        $entry = $this->collection->getFirstItem();

        //dd($entry);


        //$this->loadedData['1'] = ['pippo' => $entry->getData()];
        $this->loadedData['1'] = ['data' => $entry->getData()];
        //$this->loadedData['1'] = $entry->getData();

        dd($this->loadedData);

        return $this->loadedData;
    }
}
