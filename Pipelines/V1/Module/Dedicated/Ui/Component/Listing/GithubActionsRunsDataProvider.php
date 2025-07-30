<?php
namespace Pipelines\Dedicated\Ui\Component\Listing;

use Magento\Framework\Data\Collection\EntityFactoryInterface;
use Magento\Ui\DataProvider\AbstractDataProvider;
use Magento\Framework\App\RequestInterface;
use Magento\Framework\App\ObjectManager;

use Core\Logger\Facade\LoggerFacade;

use Pipelines\Dedicated\Model\Listing\GitHubRunsActionsCollection;
use Pipelines\Dedicated\Service\GithubActionsService;
use Pipelines\Dedicated\Helper\DedicatedHelper;

class GithubActionsRunsDataProvider extends AbstractDataProvider
{
    protected $githubActionsService;
    protected $collection;
    private RequestInterface $request;
    private string $organization;

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
        $this->organization = DedicatedHelper::getPipelinesDedicatedGithubOrganization();

        // Recupera session in modo statico da ObjectManager
        $objectManager = ObjectManager::getInstance();
        $session = $objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class);

        $pipelineId = $session->getData('pipeline_id');
        LoggerFacade::debug('GithubRunsActionsDataProvider::pipeline_id from session', [
            'pipeline_id' => $pipelineId
        ]);

        $result = [];
        if ($pipelineId) {
            // Recupera dati GitHub (o mocka in caso di errore)
            $runs = $this->githubActionsService->listWorkflowRunsForARepository($this->organization, $pipelineId);

            foreach ($runs as $run) {
                $result[] = [
                    'run_id'     => $run['id'],
                    'name'       => $run['name'] ?? $run['workflow_id'],
                    'status'     => $run['status'],
                    'conclusion' => $run['conclusion'],
                    'created_at' => $run['created_at'],
                    'html_url'   => $run['html_url'],
                    'pipeline_id' => $pipelineId,
                ];
            }
        }

        $this->collection = new GitHubRunsActionsCollection($entityFactory, $result);

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
