<?php
namespace Fnd\Notification\Model\Publisher;

use Fnd\Notifications\Api\Data\NotificationsDataInterface;
use Magento\Framework\MessageQueue\PublisherInterface;

class NotificationPublisher
{
    const TOPIC_NAME = 'fnd.topic.notifications';

    protected $publisher;

    public function __construct(PublisherInterface $publisher)
    {
        $this->publisher = $publisher;
    }

    public function send(NotificationsDataInterface $data)
    {
        $this->publisher->publish(self::TOPIC_NAME, $data);
    }
}
