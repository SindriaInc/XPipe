<?php
namespace Support\TicketForm\Ui\Component\Form;

use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\App\ObjectManager;
use Magento\Framework\Data\Collection\EntityFactoryInterface;
use Magento\Ui\DataProvider\AbstractDataProvider;
use Support\TicketForm\Model\TicketForm;
use Support\TicketForm\Model\Form\TicketFormCollection;
use Support\TicketForm\Service\GithubActionsService;


class TicketFormDataProvider extends AbstractDataProvider
{

    protected $collection;

    /**
     * @var array
     */
    public $loadedData;

    private $ticketId;
    private $username;
    private array $ticketTypes;

    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,
        EntityFactoryInterface $entityFactory,
        array $meta = [],
        array $data = []
    ) {
        LoggerFacade::debug('TicketFormDataProvider::__construct', [
            'name' => $name,
            'primaryFieldName' => $primaryFieldName,
            'requestFieldName' => $requestFieldName
        ]);


        // Recupera session in modo statico da ObjectManager
        $objectManager = ObjectManager::getInstance();
        $session = $objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class);

        // If session key does not exist, return null as magento expected to render form default parameters without id.
        // It is not a bug, it's a feature.
        $this->ticketId = $session->getData('ticket_id');
        LoggerFacade::debug('TicketFormDataProvider::ticket_id from session', [
            'ticket_id' => $this->ticketId
        ]);

        $this->username = $session->getData('username');
        LoggerFacade::debug('TicketFormDataProvider::username from session', [
            'username' => $this->username
        ]);

        $this->ticketTypes[0] = ['label' => 'Support', 'value' => 'support'];
        $this->ticketTypes[1] = ['label' => 'Dedicated Pipeline', 'value' => 'dedicated-pipeline'];
        $this->ticketTypes[2] = ['label' => 'Iam', 'value' => 'iam'];
        $this->ticketTypes[3] = ['label' => 'Incident', 'value' => 'incident'];

        $form = \Support\TicketForm\Model\TicketForm::getInstance();


        $form(
            $this->ticketId,
            'Besteam',
            $this->username,
            '',
            $this->ticketTypes,
            ''
        );


        $this->collection = new TicketFormCollection($entityFactory, $form);

        parent::__construct($name, $primaryFieldName, $requestFieldName, $meta, $data);
    }

    public function getData() : array
    {
        $entry = [];

        $entry = $this->collection->getFirstItem();

        $this->loadedData[$this->ticketId] = $entry->getData();

        return $this->loadedData;
    }
}
