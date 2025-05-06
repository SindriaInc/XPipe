<?php

namespace Sindria\SampleApi\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\Controller\ResultInterface;

use Sindria\SampleApi\Service\Api\Client;

class MassDelete extends Action
{

    private Client $client;

    public function __construct(Context $context, Client $client)
    {
        parent::__construct($context);
        $this->client = $client;
    }

    public function execute() : ResultInterface
    {

        try {
            $selectedItems = $this->getRequest()->getParams()['selected'] ?? [];
            $excludedItems = $this->getRequest()->getParams()['excluded'] ?? [];
            if ($excludedItems == 'false') {
                //TODO: risolvere workaroud per assenza dell'input (id) dei dati selezionati
                $data = $this->client->getAll();

                $selectedItems = array_column($data['data'], 'id');

                // force type
                $excludedItems = [];
            }
            $items = array_merge($selectedItems, $excludedItems);

            $successfulDeletes = [];
            $errorMessages = [];

            foreach ($items as $itemId) {
                $response = $this->client->delete($itemId);

                if (!empty($response['success'])) {
                    $successfulDeletes[] = $itemId;
                } else {
                    $error = $response['error'] ?? 'Unknown error';
                    $errorMessages[] = __("Item ID %1: %2", $itemId, $error);
                }
            }

            if (!empty($successfulDeletes)) {
                $this->messageManager->addSuccessMessage(
                    __('A total of %1 record(s) have been successfully deleted.', count($successfulDeletes))
                );
            }

            foreach ($errorMessages as $message) {
                $this->messageManager->addErrorMessage($message);
            }

        } catch (\Exception $e) {
            $this->messageManager->addErrorMessage(__('There was an error while deleting the entries: %1', $e->getMessage()));
        }

        $result = $this->resultFactory->create(ResultFactory::TYPE_REDIRECT);

        return $result->setPath('sampleapi/index/index');
    }
}