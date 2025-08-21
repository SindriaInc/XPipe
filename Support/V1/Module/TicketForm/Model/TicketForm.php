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

    private string $organization;

    private string $username;
    private string $title;
    private array $ticketTypes;
    private string $description;


    private function __construct(array $data = [])
    {
        parent::__construct($data);
    }

    public function __invoke(
        $ticketId,
        string $organization,
        string $username,
        string $title,
        array $ticketTypes,
        string $description
    )
    {
        $this->ticketId = $ticketId;
        $this->organization = $organization;
        $this->username = $username;
        $this->title = $title;
        $this->ticketTypes = $ticketTypes;
        $this->description = $description;

        $data = [];

        $data['ticket_id'] = $ticketId;
        $data['organization'] = $organization;
        $data['username'] = $username;
        $data['title'] = $title;
        $data['ticket_types'] = $this->ticketTypes;
        $data['description'] = $description;

        $this->setData($data);
    }

    public function getTicketId() : int
    {
        return $this->ticketId;
    }

    public function getOrganization() : string
    {
        return $this->organization;
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

    public function getTicketTypes() : array
    {
        return $this->ticketTypes;
    }


}