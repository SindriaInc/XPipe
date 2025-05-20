<?php
namespace Core\Notifications\Controller\Api;

use Magento\Framework\Controller\Result\JsonFactory;
use Magento\Framework\App\RequestInterface;
use Core\Notifications\Helper\Data;

class Receive
{
    protected $resultJsonFactory;
    protected $helper;
    protected $request;

    public function __construct(
        JsonFactory $resultJsonFactory,
        Data $helper,
        RequestInterface $request
    ) {
        $this->resultJsonFactory = $resultJsonFactory;
        $this->helper = $helper;
        $this->request = $request;
    }

    /**
     * Load the page defined in view/adminhtml/layout/exampleadminnewpage_helloworld_index.xml
     *
     * @return \Magento\Framework\Controller\Result\Json
     */
    public function execute(): \Magento\Framework\Controller\Result\Json
    {

        $result = $this->resultJsonFactory->create();
        try {
            $payload = json_decode($this->request->getContent(), true);

            $title = $payload['title'] ?? 'Untitled';
            $message = $payload['message'] ?? '';
            $severity = $payload['severity'] ?? 'notice';
            $source = $payload['source'] ?? '';

            $this->helper->addNotification($title, $message, $severity, '', $source);

            return $result->setData(['success' => true]);
        } catch (\Exception $e) {
            return $result->setData(['success' => false, 'message' => 'Internal error']);
        }
    }
}
