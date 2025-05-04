<?php

namespace Sindria\SampleApi\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\Controller\ResultInterface;
use Zend\Http\Client as HttpClient;
use Zend\Http\Request;
use Sindria\SampleApi\Ui\SampleApi\DataProvider;

class Delete extends Action
{


    private DataProvider $dataProvider;


    public function __construct(Context $context, DataProvider $dataProvider)
    {
        parent::__construct($context);

        $this->dataProvider = $dataProvider;
    }

    public function execute() : ResultInterface
    {

        $data = current($this->dataProvider->getData());

        $result = $this->resultFactory->create(ResultFactory::TYPE_REDIRECT);

        if (!$data && !$data['data']['id']) {
            $this->messageManager->addWarningMessage(__('Entry not found.'));
            return $result->setPath('sampleapi/index/index');
        }

        try {
            $client = new HttpClient();
            $client->setHeaders(['Content-Type' => 'application/json']);
            $client->setOptions(['timeout' => 10]);

            $client->setUri("https://api.restful-api.dev/objects/" . $data['data']['id']);
            $client->setMethod(Request::METHOD_DELETE);

            $response = $client->send();



            if ($response->isSuccess()) {

                $message = json_decode($response->getBody(), true);

                $this->messageManager->addSuccessMessage(__('Record successfully deleted via API. Message: ' . $message['message']));
            } else {
                $this->messageManager->addErrorMessage(__('API error: ' . $response->getStatusCode() . ' - ' . $response->getReasonPhrase() . ' - ' . $response->getBody()));
            }
        } catch (\Exception $e) {
            $this->messageManager->addErrorMessage(__('There was an error while deleting the entry!'));
        }

        return $result->setPath('sampleapi/index/index');
    }
}