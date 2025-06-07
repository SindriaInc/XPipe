<?php

namespace Pipelines\Configmap\Controller\Adminhtml\Index;

use Core\Logger\Facade\LoggerFacade;
use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\App\ObjectManager;
use Magento\Framework\Controller\ResultFactory;
use Pipelines\Configmap\Service\ConfigmapVaultService;


class Delete extends Action
{

    const ADMIN_RESOURCE = 'Pipelines_Configmap::configmapdelete';
    private ConfigmapVaultService $configmapVaultService;


    public function __construct(Context $context, ConfigmapVaultService $configmapVaultService)
    {
        $this->configmapVaultService = $configmapVaultService;
        parent::__construct($context);


    }

    public function execute()
    {
        $resultRedirect = $this->resultFactory->create(ResultFactory::TYPE_REDIRECT);

        // Recupera session in modo statico da ObjectManager
        $objectManager = ObjectManager::getInstance();
        $session = $objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class);

        $configmapId = $session->getData('configmap_id');
        LoggerFacade::debug('Configmap delete action::configmap_id from session', [
            'configmap_id' => $configmapId
        ]);

        $owner = $session->getData('owner') ?? 'new-owner';
        LoggerFacade::debug('Configmap delete action::owner from session', [
            'owner' => $owner
        ]);

//
//        if ($configmapId === 'new-configmap' || $configmapId === 'xpipe-iaas' || $configmapId === 'xpipe-saas') {
//            $this->messageManager->addErrorMessage(
//                __('Configmap with id %1 is reserved and cannot be deleted.', $configmapId)
//            );
//
//            LoggerFacade::error('Configmap is reserved and cannot be deleted.', ['configmap_id' => $configmapId]);
//
//            return $resultRedirect->setPath('configmap/index/index', ['configmap_id' => 'new-configmap', 'owner' => $owner]);
//        }

        if ($configmapId === 'new-configmap') {
            $this->messageManager->addErrorMessage(
                __('Configmap with id %1 is reserved and cannot be deleted.', $configmapId)
            );

            LoggerFacade::error('Configmap is reserved and cannot be deleted.', ['configmap_id' => $configmapId]);

            return $resultRedirect->setPath('configmap/index/index', ['configmap_id' => 'new-configmap', 'owner' => $owner]);
        }

        $result = $this->configmapVaultService->deleteSecret($owner, $configmapId);

        if ($result['success'] === true) {
            $this->messageManager->addSuccessMessage(
                __('Configmap %1 deleted successfully.', $result['configmap_name']),
            );
            LoggerFacade::debug('Configmap deleted successfully', [
                'configmap_name' => $result['configmap_name'],

            ]);

            return $resultRedirect->setPath('configmap/index/index', ['configmap_id' => 'new-configmap', 'owner' => $owner]);
        }

        $this->messageManager->addErrorMessage(
            __('Error while deleting the configmap.')
        );

        LoggerFacade::error('Error while deleting the configmap.');

        return $resultRedirect->setPath('configmap/index/index', ['configmap_id' => 'new-configmap', 'owner' => $owner]);
    }
}