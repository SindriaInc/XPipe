<?php
namespace Pipe\PostLoginSetup\Observer;

use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\App\ObjectManager;
use Magento\Framework\Event\ObserverInterface;
use Magento\Framework\Event\Observer;
use Magento\Backend\Model\Auth\Session as AuthSession;
use Pipe\PostLoginSetup\Service\PostLoginSetupIamService;
use Pipe\PostLoginSetup\Service\PostLoginSetupVaultService;


class AdminLoginSucceeded implements ObserverInterface
{

    private AuthSession $authSession;

    private PostLoginSetupVaultService  $postLoginSetupVaultService;
    private PostLoginSetupIamService   $postLoginSetupIamService;

    /**
     * Application Event Dispatcher
     *
     * @var \Magento\Framework\Event\ManagerInterface
     */
    protected $_eventManager;

    public function __construct(
        AuthSession $authSession,
        PostLoginSetupVaultService $postLoginSetupVaultService,
        PostLoginSetupIamService $postLoginSetupIamService,
        \Magento\Framework\Event\ManagerInterface  $eventManager,
    )
    {
        $this->authSession = $authSession;
        $this->postLoginSetupVaultService = $postLoginSetupVaultService;
        $this->postLoginSetupIamService = $postLoginSetupIamService;
        $this->_eventManager = $eventManager;
    }

    public function execute(Observer $observer)
    {
        LoggerFacade::info('AdminLoginSucceeded::execute - triggered event');

        $username = $this->authSession->getUser()->getUserName();

        $mountExists = $this->postLoginSetupVaultService->mountExists($username);

        if ($mountExists === false) {
            $response = $this->postLoginSetupVaultService->enableKvMount($username, 'Private KV tenant for ' . $username);
            // TODO: gestire error code diverso da 200/202/204
        }

        $groupsServiceResponse = $this->postLoginSetupIamService->attachUserToDefaultGroups($username);
        if ($groupsServiceResponse["code"] === 503) {
            // Recupera session in modo statico da ObjectManager
            $objectManager = ObjectManager::getInstance();
            $session = $objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class);


            $errorMessages[] = "Unable to attach default group to logged user. Groups Service Unavailable.";
            $session->setData('warning_messages', $errorMessages);
        }

        if ($groupsServiceResponse['success'] === false) {
            LoggerFacade::info('AdminLoginSucceeded::execute ' . $groupsServiceResponse['message'],
                ['statusCode' => $groupsServiceResponse['code'], 'success' => $groupsServiceResponse['success']]);
        } else {
            LoggerFacade::info('AdminLoginSucceeded::execute ' . $groupsServiceResponse['data']['message'],
                ['statusCode' => $groupsServiceResponse['code'], 'success' => $groupsServiceResponse['success']]);
        }

        $this->_eventManager->dispatch('after_post_login_setup_succeeded', ['object' => $this]);

    }
}
