<?php
namespace Pipe\AmqpNotifications\Model\Message;

use Magento\Framework\MessageQueue\PublisherInterface;

class Producer
{
    protected $publisher;

    public function __construct(PublisherInterface $publisher)
    {
        $this->publisher = $publisher;
    }

    public function send(array $data): void
    {
        $this->publisher->publish(Broker::TOPIC_NAME, json_encode($data));
    }
}
