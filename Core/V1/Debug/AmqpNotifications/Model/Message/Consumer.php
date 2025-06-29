<?php
namespace Pipe\AmqpNotifications\Model\Message;

use Psr\Log\LoggerInterface;

class Consumer
{
    protected $logger;

    public function __construct(LoggerInterface $logger)
    {
        $this->logger = $logger;
    }

    /**
     * @param array $message
     * @return void
     */
    public function process(array $message): void
    {
        dd($message);

        $data = json_decode($message, true);
        $this->logger->info('[AMQP] Notification received:', $data);
        // Aggiungi logica di business qui
    }
}
