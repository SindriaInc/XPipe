<?php
namespace Pipelines\PipeManager\Model;

use Magento\Ui\DataProvider\AbstractDataProvider;
use Pipelines\PipeManager\Service\GithubActionsService;
use Magento\Framework\Data\Collection as DataCollection;
use Magento\Framework\DataObject;
use Magento\Framework\Data\Collection\EntityFactoryInterface;

class GithubActionsDataProvider extends AbstractDataProvider
{
    protected $githubActionsService;
    protected $collection;

    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,
        GithubActionsService $githubActionsService,
        EntityFactoryInterface $entityFactory,
        array $meta = [],
        array $data = []
    ) {
        $this->githubActionsService = $githubActionsService;

        // Recupera dati GitHub (o mocka in caso di errore)
        $runs = $this->githubActionsService->getLatestRuns();
        $this->collection = new DataCollection($entityFactory);

        foreach ($runs as $run) {
            $this->collection->addItem(new DataObject([
                'run_id'     => $run['id'],
                'name'       => $run['name'] ?? $run['workflow_id'],
                'status'     => $run['status'],
                'conclusion' => $run['conclusion'],
                'created_at' => $run['created_at'],
                'html_url'   => $run['html_url'],
            ]));
        }

        parent::__construct($name, $primaryFieldName, $requestFieldName, $meta, $data);
    }

    public function getData()
    {
        $items = [];
        foreach ($this->collection->getItems() as $item) {
            $items[] = $item->getData();
        }

        return [
            'items' => $items,
            'totalRecords' => count($items)
        ];
    }
}
