<?php


namespace Fnd\Notifications\Api\Data;


interface NotificationsDataInterface
{
    /**
     * @param string $data
     * @return void
     *
     */
    public function setData(string $data) : void;

    /**
     * @return string
     */
    public function getData(): string;

}