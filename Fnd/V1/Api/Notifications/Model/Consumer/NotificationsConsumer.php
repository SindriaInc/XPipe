<?php


namespace Fnd\Notifications\Model\Consumer;

use Fnd\Notifications\Api\Data\NotificationsDataInterface;

class NotificationsConsumer
{
    /**
     * @param array $data
     * @return void
     */
    public function process($data)
    {

        dd('Notification Consumer doing stuff', $data);
        // Logica del job
        // $data sarà l'array che invii nel messaggio
    }
}
