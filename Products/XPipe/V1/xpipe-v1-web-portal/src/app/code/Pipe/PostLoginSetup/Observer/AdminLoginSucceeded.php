<?php
namespace Pipe\PostLoginSetup\Observer;

use Core\Logger\Facade\LoggerFacade;
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

    public function __construct(
        AuthSession $authSession,
        PostLoginSetupVaultService $postLoginSetupVaultService,
        PostLoginSetupIamService $postLoginSetupIamService
    )
    {
        $this->authSession = $authSession;
        $this->postLoginSetupVaultService = $postLoginSetupVaultService;
        $this->postLoginSetupIamService = $postLoginSetupIamService;
    }

    public function execute(Observer $observer)
    {
        LoggerFacade::info('AdminLoginSucceeded::execute - triggered event');

        $username = $this->authSession->getUser()->getUserName();

        $mountExists = $this->postLoginSetupVaultService->mountExists($username);

        if ($mountExists === false) {
            $this->postLoginSetupVaultService->enableKvMount($username, 'Private KV tenant for ' . $username);
        }

        $attachedGroups = $this->postLoginSetupIamService->attachUserToDefaultGroups($username);

        if ($attachedGroups['success'] === false) {
            LoggerFacade::info('AdminLoginSucceeded::execute ' . $attachedGroups['message'],
                ['statusCode' => $attachedGroups['code'], 'success' => $attachedGroups['success']]);
        } else {
            LoggerFacade::info('AdminLoginSucceeded::execute ' . $attachedGroups['data']['message'],
                ['statusCode' => $attachedGroups['code'], 'success' => $attachedGroups['success']]);
        }

    }
}
