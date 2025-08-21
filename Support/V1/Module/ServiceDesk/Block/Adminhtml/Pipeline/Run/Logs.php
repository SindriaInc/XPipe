<?php


namespace Support\ServiceDesk\Block\Adminhtml\Pipeline\Run;

use Magento\Backend\Block\Template\Context;
use Magento\Framework\View\Element\Template;
use Support\ServiceDesk\Helper\ServiceDeskHelper;
use Support\ServiceDesk\Service\GithubActionsService;

class Logs extends Template
{

    private GithubActionsService $githubActionsService;
    private string $organization;

    public function __construct(Context $context, GithubActionsService $githubActionsService, array $data = [])
    {

        parent::__construct($context, $data);

        $this->githubActionsService = $githubActionsService;
        $this->organization = ServiceDeskHelper::getSupportServiceDeskGithubOrganization();
    }

    public function getLogs(): string
    {
        // Get param from request
        return $this->githubActionsService->downloadJobLogsForAWorkflowRun($this->organization, 'demo-dev-dorje', '44124456967');

    }
}

