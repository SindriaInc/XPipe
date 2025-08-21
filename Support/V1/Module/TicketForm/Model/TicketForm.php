<?php

namespace Support\TicketForm\Model;

use Magento\Framework\DataObject;

class TicketForm extends DataObject
{

    /**
     * Singleton instance
     *
     * @var object $instance
     */
    private static object $instance;

    /**
     * Get singleton instance
     *
     * @return \Support\TicketForm\Model\TicketForm
     */
    public static function getInstance() : \Support\TicketForm\Model\TicketForm
    {
        if (!isset(self::$instance)) {
            $className = __CLASS__;
            self::$instance = new $className();
        }

        return self::$instance;
    }

    /**
     * @var int|null $ticketId
     */
    private $ticketId;

    private string $tenant;

    private string $username;
    private string $title;
    private array $ticketType;
    private string $description;


    private function __construct(array $data = [])
    {
        parent::__construct($data);
    }

    public function __invoke(
               $ticketId,
        string $tenant,
        string $username,
        string $title,
        array  $ticketTypes,
        string $description
    )
    {
        $this->ticketId = $ticketId;
        $this->tenant = $tenant;
        $this->username = $username;
        $this->title = $title;
        $this->ticketType = $ticketTypes;
        $this->description = $description;

        $data = [];

        $data['ticket_id'] = $ticketId;
        $data['tenant'] = $tenant;
        $data['username'] = $username;
        $data['title'] = $title;
        $data['ticket_type'] = $this->ticketType;
        $data['description'] = $description;

        $this->setData($data);
    }

    public function getTicketId() : int
    {
        return $this->ticketId;
    }

    public function getTenant() : string
    {
        return $this->tenant;
    }

    public function getUsername() : string
    {
        return $this->username;
    }

    public function getTitle() : string
    {
        return $this->title;
    }

    public function getDescription() : string
    {
        return $this->description;
    }

    public function getTicketType() : array
    {
        return $this->ticketType;
    }


}