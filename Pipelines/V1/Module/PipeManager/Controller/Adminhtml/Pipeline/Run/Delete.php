<?php
/**
 * Copyright Sindria Inc.
 * All rights reserved.
 */


namespace Pipelines\PipeManager\Controller\Adminhtml\Pipeline\Run;

use Core\Logger\Facade\LoggerFacade;
use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\App\Action\HttpGetActionInterface;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\View\Result\Page;
use Magento\Framework\View\Result\PageFactory;
use Pipelines\PipeManager\Helper\PipeManagerHelper;
use Pipelines\PipeManager\Service\GithubActionsService;

/**
 * Class Index
 */
class Delete extends Action implements HttpGetActionInterface
{
    const ADMIN_RESOURCE = 'Pipelines_PipeManager::deleterun';

    private string $organization;

    /**
     * @var PageFactory
     */
    protected $resultPageFactory;

    private GithubActionsService $githubActionsService;
    public function __construct(
        Context     $context,
        PageFactory $resultPageFactory,
        GithubActionsService $githubIssuesService
    )
    {
        parent::__construct($context);

        $this->resultPageFactory = $resultPageFactory;
        $this->githubActionsService = $githubIssuesService;
        $this->organization = PipeManagerHelper::getPipelinesPipeManagerGithubOrganization();
    }

    /**
     * Load the page defined in view/adminhtml/layout/exampleadminnewpage_helloworld_index.xml
     *
     * @return Page
     */
    public function execute()
    {
        // TODO: implement delete call, this is a copy of the Stop action !!!!!!!
        try {

            $params = $this->getRequest()->getParams();

            $response = $this->githubActionsService->cancelAWorkflowRun(
                $this->organization,
                $params['pipeline_id'],
                $params['run_id']
            );

            $resultRedirect = $this->resultFactory->create(ResultFactory::TYPE_REDIRECT);


            if ($response->getStatusCode() !== 202) {
                $this->messageManager->addErrorMessage(
                    __('Unable to stop pipeline.')
                );

                LoggerFacade::debug('Unable to stop pipeline.', [
                    'pipeline_id' => $params['pipeline_id'],
                    'run_id' => $params['run_id']
                ]);

                return $resultRedirect->setPath('pipemanager/pipeline/index', ['pipeline_id' => $params['pipeline_id']]);
            }

            $this->messageManager->addSuccessMessage(
                __('Pipeline stopped successfully.')
            );

            LoggerFacade::info('Pipeline stopped successfully.', [
                'pipeline_id' => $params['pipeline_id'],
                'run_id' => $params['run_id']
            ]);

            return $resultRedirect->setPath('pipemanager/pipeline/index', ['pipeline_id' => $params['pipeline_id']]);



        } catch (\Exception $e) {
            $this->messageManager->addErrorMessage(
                __('Exception while stopping the pipeline.' . ' ' . $e->getMessage())
            );

            LoggerFacade::error('Exception while stopping the pipeline.' . ' ' . $e->getMessage(), [
                'pipeline_id' => $params['pipeline_id'],
                'run_id' => $params['run_id']
            ]);

            return $resultRedirect->setPath('pipemanager/pipeline/index', ['pipeline_id' => $params['pipeline_id']]);
        }
    }
}

