<?php
namespace Iam\RequestForm\Ui\Component\Form;

use Core\Logger\Facade\LoggerFacade;
use Iam\RequestForm\Helper\RequestFormHelper;
use Magento\Framework\App\ObjectManager;
use Magento\Framework\Data\Collection\EntityFactoryInterface;
use Magento\Ui\DataProvider\AbstractDataProvider;
use Iam\RequestForm\Model\Form\RequestFormCollection;


class RequestFormDataProvider extends AbstractDataProvider
{

    protected $collection;

    /**
     * @var array
     */
    public $loadedData;
    private string $tenant;
    private $ticketId;
    private $username;
    private $fullname;
    private $email;


    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,

        EntityFactoryInterface $entityFactory,
        array $meta = [],
        array $data = []
    ) {
        LoggerFacade::debug('RequestFormDataProvider::__construct', [
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
        LoggerFacade::debug('RequestFormDataProvider::ticket_id from session', [
            'ticket_id' => $this->ticketId
        ]);

        $this->username = $session->getData('username');
        LoggerFacade::debug('RequestFormDataProvider::username from session', [
            'username' => $this->username
        ]);

        $this->fullname = $session->getData('fullname');
        LoggerFacade::debug('RequestFormDataProvider::fullname from session', [
            'fullname' => $this->fullname
        ]);

        $this->email = $session->getData('email');
        LoggerFacade::debug('RequestFormDataProvider::email from session', [
            'email' => $this->email
        ]);

        $this->tenant = RequestFormHelper::getCoreConfigTenant();

        $form = \Iam\RequestForm\Model\RequestForm::getInstance();

        $form(
            $this->ticketId,
            $this->tenant,
            $this->username,
            $this->fullname,
            $this->email,
            '',
            ''
        );

        $this->collection = new RequestFormCollection($entityFactory, $form);

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
