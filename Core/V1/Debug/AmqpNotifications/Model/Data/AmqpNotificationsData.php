<?php

namespace Pipe\AmqpNotifications\Model\Data;


use Pipe\AmqpNotifications\Api\Data\AmqpNotificationsDataInterface;

class AmqpNotificationsData implements AmqpNotificationsDataInterface
{

    /**
     * @var array
     */
    protected $data;

    /**
     * @param array $data
     * @return void
     */
    public function setData(array $data): void
    {
        $this->data = $data;
    }

    /**
     * @return array
     */
    public function getData(): array
    {
        return $this->data;
    }
}
