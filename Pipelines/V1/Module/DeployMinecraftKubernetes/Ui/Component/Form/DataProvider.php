<?php
namespace Pipelines\DeployMinecraftKubernetes\Ui\Component\Form;

use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\Data\Collection\EntityFactoryInterface;
use Magento\Ui\DataProvider\AbstractDataProvider;
use Pipelines\DeployMinecraftKubernetes\Model\Form;
use Pipelines\DeployMinecraftKubernetes\Model\Form\Collection;


class DataProvider extends AbstractDataProvider
{

    private const OWNER = 'XPipePipelines';

    protected $githubActionsService;
    protected $collection;

    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,

        EntityFactoryInterface $entityFactory,
        array $meta = [],
        array $data = []
    ) {
        LoggerFacade::debug('GithubActionsDataProvider::__construct', [
            'name' => $name,
            'primaryFieldName' => $primaryFieldName,
            'requestFieldName' => $requestFieldName
        ]);





////        $result = [];
//
//        foreach ($pipelines as $pipeline) {
//            $result[] = [
//                'pipeline_id' => $pipeline['id'],
//                'name'        => $pipeline['name'],
//                'full_name'   => $pipeline['full_name'],
//                'created_at'  => $pipeline['created_at'],
//                'updated_at'  => $pipeline['updated_at'],
//                'pushed_at'   => $pipeline['pushed_at'],
//            ];
//        }

//        $result = [
//            'field1' => 'pippo',
//            'field2' => 'gesu',
//            'field3' => [
//                '1' => 'test1',
//                '2' => 'test2',
//            ],
//        ];

        $form = new Form();
        $form->setServerName('Pippo');

        $this->collection = new Collection($entityFactory, $form);

        parent::__construct($name, $primaryFieldName, $requestFieldName, $meta, $data);
    }

    public function getData()
    {


        //TODO: get first item from collection. Check Sample api, probably return array.

        $entry = $this->collection->getFirstItem()->getData();
        dd($entry);



//        $items = [];
//        foreach ($this->collection->getItems() as $item) {
//            $items[] = $item->getData();
//        }
//
//        return [
//            'items' => $items,
//            'totalRecords' => count($items)
//        ];
    }
}
