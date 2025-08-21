<?php


namespace Support\ServiceDesk\Block\Adminhtml\Pipeline\Run;

use Magento\Backend\Block\Template\Context;
use Magento\Framework\View\Element\Template;
use Support\ServiceDesk\Helper\ServiceDeskHelper;
use Support\ServiceDesk\Service\GithubIssuesService;

class Logs extends Template
{

    private GithubIssuesService $githubIssuesService;
    private string $organization;

    public function __construct(Context $context, GithubIssuesService $githubIssuesService, array $data = [])
    {

        parent::__construct($context, $data);

        $this->githubIssuesService = $githubIssuesService;
        $this->organization = ServiceDeskHelper::getSupportServiceDeskTenant();
    }

    public function getLogs(): string
    {
        // Get param from request
        return $this->githubIssuesService->downloadJobLogsForAWorkflowRun($this->organization, 'demo-dev-dorje', '44124456967');

    }
}

