<?php


namespace Pipelines\PipeManager\Block\Adminhtml\Pipeline\Run;

use Magento\Backend\Block\Template\Context;
use Magento\Framework\View\Element\Template;
use Pipelines\PipeManager\Service\GithubActionsService;

class Logs extends Template
{

    const OWNER = 'XPipePipelines';
    private GithubActionsService $githubActionsService;
    public function __construct(Context $context, GithubActionsService $githubActionsService, array $data = [])
    {

        parent::__construct($context, $data);

        $this->githubActionsService = $githubActionsService;
    }

    public function getLogs(): string
    {
        // Get param from request
        return $this->githubActionsService->downloadJobLogsForAWorkflowRun(self::OWNER, 'demo-dev-dorje', '44124456967');

    }
}

