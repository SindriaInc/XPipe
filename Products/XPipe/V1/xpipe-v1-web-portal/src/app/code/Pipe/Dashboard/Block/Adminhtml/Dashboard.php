<?php

namespace Pipe\Dashboard\Block\Adminhtml;

use Magento\Backend\Block\Template\Context;
use Magento\Framework\App\ObjectManager;
use Magento\Framework\View\Element\Template;
use Pipelines\Configmap\Service\ConfigmapGroupService;

class Dashboard extends Template
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
     * @return void
     */
    public function consumeErrorMessages() : void
    {

        $objectManager = ObjectManager::getInstance();
        $session = $objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class);

        $successMessages = $session->getData('success_messages');
        $noticeMessages = $session->getData('notice_messages');
        $warningMessages = $session->getData('warning_messages');
        $errorMessages = $session->getData('error_messages');

        if (!empty($warningMessages)) {
            foreach ($warningMessages as $warningMessage) {
                $this->messageManager->addWarningMessage($warningMessage);
            }
        }

        $session->unsetData('warning_messages');

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

