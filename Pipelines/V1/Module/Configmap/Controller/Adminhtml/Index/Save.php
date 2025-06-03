<?php
/**
 * Copyright Sindria Inc.
 * All rights reserved.
 */


namespace Pipelines\Configmap\Controller\Adminhtml\Index;

use Core\Logger\Facade\LoggerFacade;
use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\App\Action\HttpPostActionInterface;
use Magento\Framework\App\ObjectManager;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\View\Result\Page;
use Magento\Framework\View\Result\PageFactory;
use Pipelines\Configmap\Service\ConfigmapVaultService;

/**
 * Class Index
 */
class Save extends Action implements HttpPostActionInterface
{
    const ADMIN_RESOURCE = 'Pipelines_Configmap::configmap';

    /**
     * @var PageFactory
     */
    protected $resultPageFactory;

    private ConfigmapVaultService $configmapVaultService;

    /**
     * Index constructor.
     *
     * @param Context $context
     * @param PageFactory $resultPageFactory
     */
    public function __construct(
        Context     $context,
        PageFactory $resultPageFactory,
        ConfigmapVaultService $configmapVaultService
    )
    {
        parent::__construct($context);

        $this->resultPageFactory = $resultPageFactory;
        $this->configmapVaultService = $configmapVaultService;
    }

    /**
     * Load the page defined in view/adminhtml/layout/exampleadminnewpage_helloworld_index.xml
     *
     * @return Page
     */
    public function execute()
    {
        $resultRedirect = $this->resultFactory->create(ResultFactory::TYPE_REDIRECT);

        // Recupera session in modo statico da ObjectManager
        $objectManager = ObjectManager::getInstance();
        $session = $objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class);

        $data = $this->getRequest()->getPostValue();

        // Custom validation for configmap name
        $configmapName = $data['configmap_name'];

        if (!preg_match('/^[a-zA-Z0-9]+$/', $configmapName)) {
            $this->messageManager->addErrorMessage(
                __('The name used for this configmap is unsupported')
            );

            LoggerFacade::error('Save::Validation error - The name used for this configmap is unsupported');

            return $resultRedirect->setPath('configmap/index/index', [
                'configmap_id' => 'new-configmap',
                'owner' => $session->getData('owner')
            ]);
        }



        $result = $this->configmapVaultService->saveSecret($data);


        if ($result['success'] === true) {
            $this->messageManager->addSuccessMessage(
                __('Configmap %1 saved successfully.', $result['configmap_name']),
            );
            LoggerFacade::debug('Configmap saved successfully', [
                'configmap_name' => $result['configmap_name'],

            ]);

            return $resultRedirect->setPath('configmap/index/index', ['configmap_id' => $result['configmap_id'], 'owner' => $session->getData('owner')]);
        }


        $this->messageManager->addErrorMessage(
            __('Error while saving the configmap.')
        );

        LoggerFacade::error('Error while saving the configmap.');

        return $resultRedirect->setPath('configmap/index/index', ['configmap_id' => 'new-configmap', 'owner' => $session->getData('owner')]);

    }
}

