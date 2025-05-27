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
        $entry = [];

        $entry['1']['data'] = $this->collection->getFirstItem()->getData();
//        dd($entry);
        return $entry;
    }
}
