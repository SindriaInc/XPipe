<?php

namespace Pipelines\Configmap\Block\Adminhtml;

use Core\Logger\Facade\LoggerFacade;
use Magento\Backend\Block\Template\Context;
use Magento\Framework\App\ObjectManager;
use Magento\Framework\View\Element\Template;
use Pipelines\Configmap\Service\ConfigmapGroupService;

class Configmap extends Template
{

    private $authSession;

    private $configmapVaultService;

    private ConfigmapGroupService  $configmapGroupService;
    private \Magento\Framework\Message\ManagerInterface $messageManager;

    public function __construct(
        Context $context,
        \Magento\Backend\Model\Auth\Session $authSession,
        \Pipelines\Configmap\Service\ConfigmapVaultService $configmapVaultService,
        \Magento\Framework\Message\ManagerInterface $messageManager,
        \Pipelines\Configmap\Service\ConfigmapGroupService  $configmapGroupService,

        array $data = [])
    {
        $this->authSession = $authSession;
        $this->configmapVaultService = $configmapVaultService;
        $this->configmapGroupService = $configmapGroupService;
        $this->messageManager = $messageManager;
        parent::__construct($context, $data);

    }

    /**
     * @return string
     */
    public function getConfigmapId(): string
    {
        // Recupera session in modo statico da ObjectManager
        $objectManager = ObjectManager::getInstance();
        $session = $objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class);

        LoggerFacade::debug('Configmap block:: session ', [
            'session' => $session->getData()
        ]);

        // If session key does not exist, return null as magento expected to render form default parameters without id.
        // It is not a bug, it's a feature.
        $configmapId = $session->getData('configmap_id');
        LoggerFacade::debug('Configmap block::configmap_id from session', [
            'configmap_id' => $configmapId
        ]);

        return $configmapId;
    }


    /**
     * @return string
     */
    public function getCurrentOwner(): string
    {
        // Recupera session in modo statico da ObjectManager
        $objectManager = ObjectManager::getInstance();
        $session = $objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class);

        LoggerFacade::debug('Configmap block:: session ', [
            'session' => $session->getData()
        ]);

        // If session key does not exist, return null as magento expected to render form default parameters without id.
        // It is not a bug, it's a feature.
        $owner = $session->getData('owner');
        LoggerFacade::debug('Configmap block::owner from session', [
            'owner' => $owner
        ]);

        return $owner;
    }

    /**
     * @return \Magento\User\Model\User|null
     */
    public function getCurrentUser()
    {
        return $this->authSession->getUser();
    }

    /**
     * @return mixed
     */
    public function getConfigmaps()
    {
        if ($this->configmapVaultService->mountExists($this->getCurrentOwner()) === false) {

            $owner = $this->getCurrentOwner();

            $this->messageManager->addWarningMessage('Tenant ' . $owner . ' not configured yet on the Vault.');
        }

       return $this->configmapVaultService->listConfigmaps($this->getCurrentOwner());
    }

    public function getAttachedGroupsToUser(): array
    {

        $userGroup[] = [
            'slug' => $this->getCurrentUser()->getUserName(),
            'label' => $this->getCurrentUser()->getFirstname() . ' ' . $this->getCurrentUser()->getLastname(),
            'short' => ''
        ];

        $attachedGroups = $this->configmapGroupService->getAttachedGroups($this->getCurrentUser()->getUserName());

        return array_merge($userGroup, $attachedGroups);
    }

    public function getMessages()
    {
        $messages = array();
        $collection = $this->messageManager->getMessages(true);
        if ($collection && $collection->getItems()) {
            foreach ($collection->getItems() as $message) {
                $messages[] = $message;
            }
        }
        
        return $messages;
    }
}

