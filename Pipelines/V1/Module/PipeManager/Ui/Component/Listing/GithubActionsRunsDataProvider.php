<?php
namespace Pipelines\PipeManager\Ui\Component\Listing;

use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\Data\Collection\EntityFactoryInterface;
use Magento\Ui\DataProvider\AbstractDataProvider;
use Pipelines\PipeManager\Model\Listing\GitHubRunsActionsCollection;
use Pipelines\PipeManager\Service\GithubActionsService;
use Magento\Framework\App\RequestInterface;

class GithubActionsRunsDataProvider extends AbstractDataProvider
{
    const OWNER = 'XPipePipelines';

    protected $githubActionsService;
    protected $collection;
    private RequestInterface $request;

    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,
        GithubActionsService $githubActionsService,
        EntityFactoryInterface $entityFactory,
        RequestInterface $request,
        array $meta = [],
        array $data = []
    ) {
        LoggerFacade::debug('GithubRunsActionsDataProvider::__construct', [
            'name' => $name,
            'primaryFieldName' => $primaryFieldName,
            'requestFieldName' => $requestFieldName
        ]);

        $this->githubActionsService = $githubActionsService;
        $this->request = $request;

        $repoName = $this->request->getParam('pipeline_id');
//        dd($repoName);

        // Recupera dati GitHub (o mocka in caso di errore)
        $runs = $this->githubActionsService->listWorkflowRunsForARepository(self::OWNER, $repoName);

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

        $this->collection = new GitHubRunsActionsCollection($entityFactory, $result);

//        dd($this->collection);

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
