<?php
namespace Pipelines\PipeManager\Ui\Component\Listing;

use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\Data\Collection as DataCollection;
use Magento\Framework\Data\Collection\EntityFactoryInterface;
use Magento\Framework\DataObject;
use Magento\Ui\DataProvider\AbstractDataProvider;
use Pipelines\PipeManager\Model\Listing\GitHubActionsCollection;
use Pipelines\PipeManager\Service\GithubActionsService;

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
        LoggerFacade::debug('GithubActionsDataProvider::__construct', [
            'name' => $name,
            'primaryFieldName' => $primaryFieldName,
            'requestFieldName' => $requestFieldName
        ]);



        $this->githubActionsService = $githubActionsService;

        // Recupera dati GitHub (o mocka in caso di errore)
        $runs = $this->githubActionsService->listWorkflowRunsForARepository();



//        $result = [];

        foreach ($runs as $run) {
            $result[] = [
                'run_id'     => $run['id'],
                'name'       => $run['name'] ?? $run['workflow_id'],
                'status'     => $run['status'],
                'conclusion' => $run['conclusion'],
                'created_at' => $run['created_at'],
                'html_url'   => $run['html_url'],
            ];
        }

        $this->collection = new GitHubActionsCollection($entityFactory, $result);

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
