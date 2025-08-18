<?php

namespace Pipelines\DedicatedForm\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Framework\App\Action\HttpPostActionInterface;
use Magento\Framework\Controller\ResultInterface;
use Magento\Framework\Controller\Result\Redirect;
use Academy\SampleApi\Service\Api\Client;
use Pipelines\DedicatedForm\Service\GithubActionsService;

class Submit extends Action implements HttpPostActionInterface
{
    protected GithubActionsService $githubActionsService;


//    const ADMIN_RESOURCE = 'Pipelines_TemplateStore::goincluded';

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
//        dd($data);

        /** @var Redirect $resultRedirect */
        $resultRedirect = $this->resultRedirectFactory->create();

        if (!$data) {
            $this->messageManager->addErrorMessage(__('No data found.'));
            return $resultRedirect->setPath('*/*/');
        }


        $result = $this->githubActionsService->createIssueForProject('SindriaInc', 'XPipe', 5,  $data);
        dd($result);
//
//        $payload = [
//            'name' => $data['data']['name'] ?? '',
//            'data' => [
//                'color' => $data['data']['color'] ?? '',
//                'capacity' => $data['data']['capacity'] ?? ''
//            ]
//        ];
//
//        try {
//            if (empty($data['data']['id'])) {
//                // Update
//                $result = $this->client->create($payload);
//            } else {
//                // log error
//            }
//
//            if ($result['success']) {
//                $entry = $result['data'];
//                $this->messageManager->addSuccessMessage(__('Record successfully saved via API. Name: %1 | ID: %2', $entry['name'], $entry['id']));
//            } else {
//                $this->messageManager->addErrorMessage(__('API error: %1', $result['error']));
//            }
//
//        } catch (\Exception $e) {
//            $this->messageManager->addErrorMessage(__('Exception: %1', $e->getMessage()));
//        }
//
//        return $resultRedirect->setPath('*/*/');
    }
}
