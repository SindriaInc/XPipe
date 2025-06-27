<?php
namespace Fnd\Notification\Publisher;

use Magento\Framework\MessageQueue\PublisherInterface;

class NotificationPublisher
{
    const TOPIC_NAME = 'fnd.notification.send';

    protected $publisher;

    public function __construct(PublisherInterface $publisher)
    {
        $this->publisher = $publisher;
    }

    public function send(array $data)
    {
        $this->publisher->publish(self::TOPIC_NAME, $data);
    }
}
