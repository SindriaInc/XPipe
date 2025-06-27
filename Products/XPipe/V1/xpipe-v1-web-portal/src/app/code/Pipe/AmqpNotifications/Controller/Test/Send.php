<?php
namespace Pipe\AmqpNotifications\Controller\Test;

use Magento\Framework\App\Action\Action;
use Magento\Framework\App\Action\Context;
use Magento\Framework\Controller\Result\JsonFactory;
use Pipe\AmqpNotifications\Api\Data\AmqpNotificationsDataInterface;
use Pipe\AmqpNotifications\Model\Message\Producer;

class Send extends Action
{
    protected $jsonFactory;
    protected $producer;
    protected $amqpNotification;

    public function __construct(
        Context $context,
        JsonFactory $jsonFactory,
        Producer $producer,
        AmqpNotificationsDataInterface  $amqpNotification
    ) {
        parent::__construct($context);
        $this->jsonFactory = $jsonFactory;
        $this->producer = $producer;
        $this->amqpNotification = $amqpNotification;
    }

    public function execute()
    {
        $data = [
            'user_id' => rand(1, 1000),
            'message' => 'Test AMQP message from controller',
            'timestamp' => date('c')
        ];

        $this->amqpNotification->setData($data);
        $this->producer->send($this->amqpNotification);

        $result = $this->jsonFactory->create();
        return $result->setData([
            'status' => 'ok',
            'message' => 'Sent to queue',
            'data' => $data
        ]);
    }
}
