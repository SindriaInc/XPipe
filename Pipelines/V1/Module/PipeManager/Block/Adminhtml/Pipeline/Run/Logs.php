<?php


namespace Pipelines\PipeManager\Block\Adminhtml\Pipeline\Run;

use Magento\Backend\Block\Template\Context;
use Magento\Framework\View\Element\Template;
use Pipelines\PipeManager\Helper\PipeManagerHelper;
use Pipelines\PipeManager\Service\GithubActionsService;

class Logs extends Template
{

    private GithubActionsService $githubActionsService;
    private string $organization;

    public function __construct(Context $context, GithubActionsService $githubIssuesService, array $data = [])
    {

        parent::__construct($context, $data);

        $this->githubActionsService = $githubIssuesService;
        $this->organization = PipeManagerHelper::getPipelinesPipeManagerGithubOrganization();
    }

    public function getLogs(): string
    {
        // Get param from request
        return $this->githubActionsService->downloadJobLogsForAWorkflowRun($this->organization, 'demo-dev-dorje', '44124456967');

    }
}

