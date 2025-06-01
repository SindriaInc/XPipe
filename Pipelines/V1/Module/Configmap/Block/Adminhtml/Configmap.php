<?php


namespace Pipelines\Configmap\Block\Adminhtml;

use Core\Logger\Facade\LoggerFacade;
use Magento\Backend\Block\Template\Context;
use Magento\Framework\App\ObjectManager;
use Magento\Framework\View\Element\Template;

class Configmap extends Template
{

    public function __construct(Context $context, array $data = [])
    {
        parent::__construct($context, $data);
    }

    public function getConfigmapId(): string
    {
        // Recupera session in modo statico da ObjectManager
        $objectManager = ObjectManager::getInstance();
        $session = $objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class);

        // If session key does not exist, return null as magento expected to render form default parameters without id.
        // It is not a bug, it's a feature.
        $configmapId = $session->getData('configmap_id');
        LoggerFacade::debug('ConfigmapDataProvider::configmap_id from session', [
            'configmap_id' => $configmapId
        ]);

        return $configmapId;

    }
}

