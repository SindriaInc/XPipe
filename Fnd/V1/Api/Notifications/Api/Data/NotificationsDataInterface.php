<?php


namespace Fnd\Notifications\Api\Data;

interface NotificationsDataInterface
{
    /**
     * @return void
     * @param string $data
     */
    public function setData(string $data) : void;

    /**
     * @return string
     */
    public function getData(): string;

}