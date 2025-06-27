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

    public function process(string $message): void
    {
        $data = json_decode($message, true);
        $this->logger->info('[AMQP] Notification received:', $data);
        // Aggiungi logica di business qui
    }
}
