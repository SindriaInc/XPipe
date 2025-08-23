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

    protected $githubIssuesService;
    protected $collection;
    private string $tenant;
    protected $_escaper;
    private string $organization;
    private string $repo;

    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,
        GithubIssuesService $githubIssuesService,
        \Magento\Framework\Escaper $_escaper,
        EntityFactoryInterface $entityFactory,
        array $meta = [],
        array $data = []
    ) {
        LoggerFacade::debug('GithubIssuesDataProvider::__construct', [
            'name' => $name,
            'primaryFieldName' => $primaryFieldName,
            'requestFieldName' => $requestFieldName
        ]);

        $this->githubIssuesService = $githubIssuesService;
        $this->_escaper = $_escaper;
        $this->tenant = ServiceDeskHelper::getCoreConfigTenant();

        $this->organization = ServiceDeskHelper::getSupportServiceDeskGitHubOrganization();
        $this->repo = ServiceDeskHelper::getSupportServiceDeskGitHubRepository();

        $ticketsResource = $this->githubIssuesService->listIssuesByOrganization($this->organization, $this->repo, $this->tenant);

        if ($ticketsResource['success'] === true && $ticketsResource['code'] == 200) {
            foreach ($ticketsResource['data'] as $ticket) {

                $ticketStatusResource = $this->githubIssuesService->getTicketStatus($ticket['node_id']);

                if ($ticketStatusResource['success'] === true && $ticketStatusResource['code'] === 200) {
                    $result[] = [
                        'ticket_id' => $ticket['number'],
                        'name' => $ticket['title'],
                        'description' => $this->_escaper->escapeHtml($ticket['body']),
                        'status' => $ticketStatusResource['data'],
                        'created_at' => date('d/m/y H:i', strtotime($ticket['created_at'])),
                        'updated_at' => date('d/m/y H:i', strtotime($ticket['updated_at'])),
                    ];
                } else {
                    $result = [];
                }
            }
        } else {
            $result = [];
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
