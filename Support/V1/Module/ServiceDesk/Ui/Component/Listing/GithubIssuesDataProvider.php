<?php
namespace Support\ServiceDesk\Ui\Component\Listing;

use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\Data\Collection\EntityFactoryInterface;
use Magento\Ui\DataProvider\AbstractDataProvider;
use Support\ServiceDesk\Helper\ServiceDeskHelper;
use Support\ServiceDesk\Model\Listing\GitHubIssuesCollection;
use Support\ServiceDesk\Service\GithubIssuesService;

class GithubIssuesDataProvider extends AbstractDataProvider
{

    protected $githubActionsService;
    protected $collection;
    private string $organization;

    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,
        GithubIssuesService $githubActionsService,
        EntityFactoryInterface $entityFactory,
        array $meta = [],
        array $data = []
    ) {
        LoggerFacade::debug('GithubTicketsDataProvider::__construct', [
            'name' => $name,
            'primaryFieldName' => $primaryFieldName,
            'requestFieldName' => $requestFieldName
        ]);

        $this->githubActionsService = $githubActionsService;
        $this->organization = ServiceDeskHelper::getSupportServiceDeskGithubOrganization();

        $tickets = $this->githubActionsService->listIssuesByOrganization('SindriaInc', 'XPipe', $this->organization);

        foreach ($tickets as $ticket) {
            $result[] = [
                'ticket_id' => $ticket['number'],
                'name'        => $ticket['title'],
                'description'   => $ticket['body'],
                'created_at'  => $ticket['created_at'],
                'updated_at'  => $ticket['updated_at'],
            ];
        }

        $this->collection = new GitHubIssuesCollection($entityFactory, $result);

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
