<?php
namespace Pipe\AmqpNotifications\Controller\Test;

use Magento\Framework\App\Action\Action;
use Magento\Framework\App\Action\Context;
use Magento\Framework\Controller\Result\JsonFactory;
use Pipe\AmqpNotifications\Model\Message\Producer;

class Send extends Action
{
    protected $jsonFactory;
    protected $producer;

    public function __construct(
        Context $context,
        JsonFactory $jsonFactory,
        Producer $producer
    ) {
        parent::__construct($context);
        $this->jsonFactory = $jsonFactory;
        $this->producer = $producer;
    }

    public function execute()
    {
        $data = [
            'user_id' => rand(1, 1000),
            'message' => 'Test AMQP message from controller',
            'timestamp' => date('c')
        ];

        $this->producer->send($data);

        $result = $this->jsonFactory->create();
        return $result->setData([
            'status' => 'ok',
            'message' => 'Sent to queue',
            'data' => $data
        ]);
    }
}
