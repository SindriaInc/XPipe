<?php

namespace Iam\RequestForm\Controller\Adminhtml\Index;

use Iam\RequestForm\Helper\RequestFormHelper;
use Magento\Backend\App\Action;
use Magento\Framework\App\Action\HttpPostActionInterface;
use Magento\Framework\Controller\ResultInterface;
use Iam\RequestForm\Service\GithubActionsService;

class Submit extends Action implements HttpPostActionInterface
{
    protected GithubActionsService $githubActionsService;

    private string $organization;
    private string $repo;
    private string $projectNumber;

    const ADMIN_RESOURCE = 'Iam_RequestForm::submit';

    public function __construct(
        \Magento\Backend\App\Action\Context $context,
        GithubActionsService $githubActionsService
    ) {
        parent::__construct($context);
        $this->githubActionsService = $githubActionsService;

        $this->organization = RequestFormHelper::getSupportServiceDeskGitHubOrganization();
        $this->repo = RequestFormHelper::getSupportServiceDeskGitHubRepository();
        $this->projectNumber = RequestFormHelper::getSupportServiceDeskGitHubProjectNumber();

    }

    public function execute(): ResultInterface
    {
        $data = $this->getRequest()->getPostValue();

        /** @var Redirect $resultRedirect */
        $resultRedirect = $this->resultRedirectFactory->create();

        if (!$data) {
            $this->messageManager->addErrorMessage(__('No data found.'));
            return $resultRedirect->setPath('*/*/');
        }

        try {
            $result = $this->githubActionsService->createIssueForProject($this->organization, $this->repo, $this->projectNumber, $data);
            if ($result['success'] === true) {
                $this->messageManager->addSuccessMessage(__('Request submitted successfully.'));
                return $resultRedirect->setPath('*/*/');
            }

            $this->messageManager->addErrorMessage(__('An error occurred while submitting your request.'));
            return $resultRedirect->setPath('*/*/');


        } catch (\Exception $e) {
            $this->messageManager->addErrorMessage(__('Unexpected error occurred while submitting your request.'));
            return $resultRedirect->setPath('*/*/');
        }


    }
}
