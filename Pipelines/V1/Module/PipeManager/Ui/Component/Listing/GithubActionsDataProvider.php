<?php
namespace Pipelines\PipeManager\Ui\Component\Listing;

use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\Data\Collection\EntityFactoryInterface;
use Magento\Ui\DataProvider\AbstractDataProvider;
use Pipelines\PipeManager\Helper\PipeManagerHelper;
use Pipelines\PipeManager\Model\Listing\GitHubActionsCollection;
use Pipelines\PipeManager\Service\GithubActionsService;

class GithubActionsDataProvider extends AbstractDataProvider
{

    protected $githubActionsService;
    protected $collection;
    private string $organization;

    public function __construct(
                               $name,
                               $primaryFieldName,
                               $requestFieldName,
        GithubActionsService   $githubIssuesService,
        EntityFactoryInterface $entityFactory,
        array                  $meta = [],
        array                  $data = []
    ) {
        LoggerFacade::debug('GithubActionsDataProvider::__construct', [
            'name' => $name,
            'primaryFieldName' => $primaryFieldName,
            'requestFieldName' => $requestFieldName
        ]);

        $this->githubActionsService = $githubIssuesService;
        $this->organization = PipeManagerHelper::getPipelinesPipeManagerGithubOrganization();

        // Recupera dati GitHub (o mocka in caso di errore)
        $pipelines = $this->githubActionsService->listOrganizationRepositories($this->organization);


        foreach ($pipelines as $pipeline) {
            $result[] = [
                'pipeline_id' => $pipeline['id'],
                'name'        => $pipeline['name'],
                'full_name'   => $pipeline['full_name'],
                'created_at'  => $pipeline['created_at'],
                'updated_at'  => $pipeline['updated_at'],
                'pushed_at'   => $pipeline['pushed_at'],
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
