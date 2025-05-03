<?php

namespace Sindria\SampleApi\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Framework\App\Action\HttpPostActionInterface;
use Zend\Http\Client as HttpClient;
use Zend\Http\Request;

class Save extends Action implements HttpPostActionInterface
{
    public function execute()
    {
        $data = $this->getRequest()->getPostValue();



        if (!$data) {
            $this->messageManager->addErrorMessage(__('No data found.'));
            return $this->resultRedirectFactory->create()->setPath('*/*/');
        }

        try {
            $client = new HttpClient();
            $client->setHeaders(['Content-Type' => 'application/json']);
            $client->setOptions(['timeout' => 10]);

            $payload = [
                'name' => $data['data']['name'] ?? '',
                'data' => [
                    'color' => $data['data']['color'] ?? '',
                    'capacity' => $data['data']['capacity'] ?? ''
                ]
            ];

            if (!empty($data['id'])) {
                // Update (PUT)
                $client->setUri("https://api.restful-api.dev/objects/" . $data['data']['id']);
                $client->setMethod(Request::METHOD_PUT);
            } else {
                // Create (POST)
                $client->setUri("https://api.restful-api.dev/objects");
                $client->setMethod(Request::METHOD_POST);
            }

            $client->setRawBody(json_encode($payload));

            $response = $client->send();

            if ($response->isSuccess()) {

                $createdEntry = json_decode($response->getBody(), true);

                $this->messageManager->addSuccessMessage(__('Record successfully saved via API. Name: ' . $createdEntry['name'] . ' ' . 'ID: ' .  $createdEntry['id']));
            } else {
                $this->messageManager->addErrorMessage(__('API error: ' . $response->getStatusCode() . ' - ' . $response->getReasonPhrase()));
            }

        } catch (\Exception $e) {
            $this->messageManager->addErrorMessage(__('Exception: ' . $e->getMessage()));
        }

        return $this->resultRedirectFactory->create()->setPath('*/*/');
    }
}
