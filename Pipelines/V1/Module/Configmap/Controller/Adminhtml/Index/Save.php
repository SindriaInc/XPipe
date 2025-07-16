<?php
/**
 * Copyright Sindria Inc.
 * All rights reserved.
 */


namespace Pipelines\Configmap\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\App\Action\HttpPostActionInterface;
use Magento\Framework\App\ObjectManager;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\View\Result\Page;
use Magento\Framework\View\Result\PageFactory;

use Core\Logger\Facade\LoggerFacade;

use Pipelines\Configmap\Service\ConfigmapVaultService;
use Pipelines\Configmap\Helper\ConfigmapHelper;

/**
 * Class Index
 */
class Save extends Action implements HttpPostActionInterface
{
    const ADMIN_RESOURCE = 'Pipelines_Configmap::save';

    /**
     * @var PageFactory
     */
    protected $resultPageFactory;

    private $authSession;

    private ConfigmapVaultService $configmapVaultService;


    /**
     * Save constructor
     *
     * @param Context $context
     * @param PageFactory $resultPageFactory
     * @param \Magento\Backend\Model\Auth\Session $authSession
     * @param ConfigmapVaultService $configmapVaultService
     */
    public function __construct(
        Context     $context,
        PageFactory $resultPageFactory,
        \Magento\Backend\Model\Auth\Session $authSession,
        ConfigmapVaultService $configmapVaultService
    )
    {
        parent::__construct($context);

        $this->resultPageFactory = $resultPageFactory;
        $this->authSession = $authSession;
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

        $currentUser = $this->authSession->getUser();

        $data = $this->getRequest()->getPostValue();

        if ($this->configmapVaultService->mountExists($data['owner']) === false) {
            $this->messageManager->addErrorMessage(
                __('Error while saving the configmap: tenant %1 not configured yet on the Vault.', $data['owner'])
            );

            LoggerFacade::error('Save::execute Tenant not configured yet on the Vault', ['tenant' => $data['owner']]);

            return $resultRedirect->setPath('configmap/index/index', [
                'configmap_id' => 'new-configmap',
                'owner' => $session->getData('owner')
            ]);
        }


        // Custom validation for configmap name
        $configmapName = $data['configmap_name'];

        if (!preg_match('/^[A-Z](([a-z0-9]+[A-Z]?)*)$/', $configmapName) && ConfigmapHelper::isSuperAdmin($currentUser) === false) {
            $this->messageManager->addErrorMessage(
                __('The name %1 used for this configmap is unsupported. The Configmap name must be Pascal case: VendorProjectEnv, for example BarillaCommerceProduction.', $configmapName)
            );

            LoggerFacade::error('Save::Validation error - The name used for this configmap is unsupported');

            return $resultRedirect->setPath('configmap/index/index', [
                'configmap_id' => 'new-configmap',
                'owner' => $session->getData('owner')
            ]);
        }


        //TODO: add warning message if the name of the config map is changed. Now the input is able to change the name but is ignored.
        // It works but in not sexy way.

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

