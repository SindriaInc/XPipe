<?php

namespace Support\TicketForm\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Framework\App\Action\HttpPostActionInterface;
use Magento\Framework\Controller\ResultInterface;
use Support\TicketForm\Helper\TicketFormHelper;
use Support\TicketForm\Service\GithubActionsService;

class Submit extends Action implements HttpPostActionInterface
{
    protected GithubActionsService $githubActionsService;

    private string $organization;
    private string $repo;
    private string $projectNumber;

    const ADMIN_RESOURCE = 'Support_TicketForm::submit';

    public function __construct(
        \Magento\Backend\App\Action\Context $context,
        GithubActionsService $githubActionsService
    ) {
        parent::__construct($context);
        $this->githubActionsService = $githubActionsService;

        $this->organization = TicketFormHelper::getSupportServiceDeskGitHubOrganization();
        $this->repo = TicketFormHelper::getSupportServiceDeskGitHubRepository();
        $this->projectNumber = TicketFormHelper::getSupportServiceDeskGitHubProjectNumber();
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
                return $resultRedirect->setPath('servicedesk/index/index');
            }

            $this->messageManager->addErrorMessage(__('An error occurred while submitting your request.'));
            return $resultRedirect->setPath('servicedesk/index/index');


        } catch (\Exception $e) {
            $this->messageManager->addErrorMessage(__('Unexpected error occurred while submitting your request.'));
            return $resultRedirect->setPath('servicedesk/index/index');
        }


    }
}
