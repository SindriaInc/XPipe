<?php

namespace Pipelines\Dedicated\Block\Adminhtml\Pipeline\Run;

use Magento\Backend\Block\Template\Context;
use Magento\Framework\App\ObjectManager;
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

        $objectManager = ObjectManager::getInstance();
        $session = $objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class);

        $pipelineId = $session->getData('pipeline_id');
        $runId = $session->getData('run_id');

        // Commented because on refresh we miss the run id to get the job id.
        //  $session->unsetData('pipeline_id');
        //  $session->unsetData('run_id');

        $response =  $this->githubActionsService->getJobId($this->organization, $pipelineId, $runId);
        if ($response['success'] === true) {

            $jobId = $response['data'];

            return $this->githubActionsService->downloadJobLogsForAWorkflowRun($this->organization, $pipelineId, $jobId);
        }

        return __('Error while getting the job logs for a workflow run.');
    }
}

