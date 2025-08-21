<?php

namespace Iam\RequestForm\Model;

use Magento\Framework\DataObject;

class RequestForm extends DataObject
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
     * @return \Iam\RequestForm\Model\RequestForm
     */
    public static function getInstance() : \Iam\RequestForm\Model\RequestForm
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
        string $description
    )
    {
        $this->ticketId = $ticketId;
        $this->tenant = $tenant;
        $this->username = $username;
        $this->title = $title;
        $this->description = $description;

        $data = [];

        $data['ticket_id'] = $ticketId;
        $data['tenant'] = $tenant;
        $data['username'] = $username;
        $data['title'] = $title;
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


}