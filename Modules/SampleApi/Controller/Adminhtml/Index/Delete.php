<?php

namespace Sindria\SampleApi\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\Controller\ResultInterface;
use Sindria\SampleApi\Ui\Component\Form\DataProvider;
use Sindria\SampleApi\Service\Api\Client;

class Delete extends Action
{
    private DataProvider $dataProvider;

    private Client $client;


    public function __construct(Context $context, DataProvider $dataProvider, Client $client)
    {
        parent::__construct($context);

        $this->client = $client;

        $this->dataProvider = $dataProvider;
    }

    public function execute() : ResultInterface
    {

        $data = current($this->dataProvider->getData());

        $result = $this->resultFactory->create(ResultFactory::TYPE_REDIRECT);

        if (!$data['data']['id']) {
            $this->messageManager->addWarningMessage(__('Entry not found.'));
            return $result->setPath('sampleapi/index/index');
        }

        try {

            $responseData = $this->client->delete($data['data']['id']);

            if ($responseData['success']) {
                $this->messageManager->addSuccessMessage(__('Record successfully deleted via API. Message: ' . $responseData['data']));
            } else {
                $this->messageManager->addErrorMessage(__('API error: ' . $responseData['error']));
            }
        } catch (\Exception $e) {
            $this->messageManager->addErrorMessage(__('There was an error while deleting the entry!'));
        }

        return $result->setPath('sampleapi/index/index');
    }
}