<?php

namespace Pipelines\Dedicated\Block\Adminhtml\Pipeline\Run;

use Magento\Backend\Block\Template\Context;
use Magento\Framework\View\Element\Template;

use Pipelines\Dedicated\Helper\DedicatedHelper;
use Pipelines\Dedicated\Service\GithubActionsService;

class Logs extends Template
{

    private GithubActionsService $githubActionsService;
    private string $organization;

    public function __construct(Context $context, GithubActionsService $githubIssuesService, array $data = [])
    {

        parent::__construct($context, $data);

        $this->githubActionsService = $githubIssuesService;
        $this->organization = DedicatedHelper::getPipelinesDedicatedGithubOrganization();
    }

    public function getLogs(): string
    {
        // Get param from request
        return $this->githubActionsService->downloadJobLogsForAWorkflowRun($this->organization, 'demo-dev-dorje', '44124456967');

    }
}

