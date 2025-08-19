<?php

namespace Support\TicketForm\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Framework\App\Action\HttpPostActionInterface;
use Magento\Framework\Controller\ResultInterface;
use Support\TicketForm\Service\GithubActionsService;

class Submit extends Action implements HttpPostActionInterface
{
    protected GithubActionsService $githubActionsService;

    const ADMIN_RESOURCE = 'Support_TicketForm::submit';

    public function __construct(
        \Magento\Backend\App\Action\Context $context,
        GithubActionsService $githubActionsService
    ) {
        parent::__construct($context);
        $this->githubActionsService = $githubActionsService;
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
            $result = $this->githubActionsService->createIssueForProject('SindriaInc', 'XPipe', 5,  $data);
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
