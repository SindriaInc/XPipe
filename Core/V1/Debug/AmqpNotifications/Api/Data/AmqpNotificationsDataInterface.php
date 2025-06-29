<?php


namespace Pipe\AmqpNotifications\Api\Data;

interface AmqpNotificationsDataInterface
{
    /**
     *
     * @param array $data
     * @return void
     */
    public function setData(array $data) : void;

    /**
     * @return array
     */
    public function getData(): array;

}
