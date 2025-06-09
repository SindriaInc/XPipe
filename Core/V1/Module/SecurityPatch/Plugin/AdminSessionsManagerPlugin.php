<?php
namespace Core\SecurityPatch\Plugin;

use Magento\Security\Model\AdminSessionsManager;
use Magento\Backend\Model\Auth\Session as AdminSession;

class AdminSessionsManagerPlugin
{
    protected $authSession;

    public function __construct(AdminSession $authSession)
    {
        $this->authSession = $authSession;
    }

    public function aroundProcessLogout(AdminSessionsManager $subject, callable $proceed)
    {
        $user = $this->authSession->getUser();

        if (!$user || !$user->getId()) {
            // Utente nullo (sessione scaduta): NON eseguire logout per evitare errore
            return;
        }

        // Continua con l'esecuzione normale
        return $proceed();
    }
}
