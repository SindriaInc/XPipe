<?php

namespace Fnd\Notifications\Model\Consumer;

use Fnd\Notifications\Api\Data\NotificationsDataInterface;

class NotificationsData implements NotificationsDataInterface
{

    /**
     * @var string
     */
    protected $data;

    /**
     * @param string $data
     * @return void
     */
    public function setData(string $data): void
    {
        $this->data = $data;
    }

    /**
     * @return string
     */
    public function getData(): string
    {
        return $this->data;
    }
}