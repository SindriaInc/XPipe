<?php
namespace Pipe\AmqpNotifications\Model\Message;

use Magento\Framework\MessageQueue\PublisherInterface;
use Pipe\AmqpNotifications\Api\Data\AmqpNotificationsDataInterface;

class Producer
{
    protected $publisher;

    public function __construct(PublisherInterface $publisher)
    {
        $this->publisher = $publisher;
    }

    public function send(AmqpNotificationsDataInterface $data): void
    {
        $this->publisher->publish(Broker::TOPIC_NAME, $data);
    }
}
