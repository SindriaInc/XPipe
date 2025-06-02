<?php

namespace Pipelines\Configmap\Block\Adminhtml;

use Core\Logger\Facade\LoggerFacade;
use Magento\Backend\Block\Template\Context;
use Magento\Framework\App\ObjectManager;
use Magento\Framework\View\Element\Template;

class Configmap extends Template
{

    private $authSession;

    private $configmapVaultService;

    public function __construct(
        Context $context,
        \Magento\Backend\Model\Auth\Session $authSession,
        \Pipelines\Configmap\Service\ConfigmapVaultService $configmapVaultService,
        array $data = [])
    {
        $this->authSession = $authSession;
        $this->configmapVaultService = $configmapVaultService;
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
       return $this->configmapVaultService->listConfigmaps($this->getCurrentUser()->getUserName());

    }
}

