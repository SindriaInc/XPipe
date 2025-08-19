<?php

namespace Pipe\Dashboard\Block\Adminhtml;

use Magento\Backend\Block\Template\Context;
use Magento\Framework\App\ObjectManager;
use Magento\Framework\View\Element\Template;

use Pipe\Dashboard\Helper\SystemEnvHelper;

class Dashboard extends Template
{

    private $authSession;

    private \Magento\Framework\Message\ManagerInterface $messageManager;

    public function __construct(
        Context $context,
        \Magento\Backend\Model\Auth\Session $authSession,
        \Magento\Framework\Message\ManagerInterface $messageManager,

        array $data = [])
    {
        $this->authSession = $authSession;
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


    public function getSuperAdminDashboard()
    {
        return SystemEnvHelper::get('PIPE_SUPERADMIN_DASHBOARD_URL'); // TODO: plausile analytics
    }

    public function getDevDashboard()
    {
        return SystemEnvHelper::get('PIPE_DEV_DASHBOARD_URL');
    }

    public function getDemoDashboard()
    {
        return SystemEnvHelper::get('PIPE_DEMO_DASHBOARD_URL'); // Metabase mockup
    }

    public function getProfileDashboard()
    {
        return SystemEnvHelper::get('PIPE_PROFILE_DASHBOARD_URL');
    }

    public function getBetaDashboard()
    {
        return SystemEnvHelper::get('PIPE_BETA_DASHBOARD_URL');
    }
}

