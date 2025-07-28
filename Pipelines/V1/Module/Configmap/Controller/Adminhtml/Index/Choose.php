<?php
/**
 * Copyright Sindria Inc.
 * All rights reserved.
 */


namespace Pipelines\Configmap\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\App\Action\HttpPostActionInterface;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\View\Result\Page;
use Magento\Framework\View\Result\PageFactory;
use Magento\Framework\App\ObjectManager;

use Core\Logger\Facade\LoggerFacade;

use Pipelines\Configmap\Service\ConfigmapVaultService;
use Pipelines\Configmap\Helper\ConfigmapHelper;

/**
 * Class Index
 */
class Choose extends Action implements HttpPostActionInterface
{
    const ADMIN_RESOURCE = 'Pipelines_Configmap::list';

    /**
     * @var PageFactory
     */
    protected $resultPageFactory;

    private $authSession;

    private ConfigmapVaultService $configmapVaultService;


    /**
     * Choose constructor
     *
     * @param Context $context
     * @param PageFactory $resultPageFactory
     * @param ConfigmapVaultService $configmapVaultService
     * @param \Magento\Backend\Model\Auth\Session $authSession
     */
    public function __construct(
        Context     $context,
        PageFactory $resultPageFactory,
        ConfigmapVaultService $configmapVaultService,
        \Magento\Backend\Model\Auth\Session $authSession
    )
    {
        parent::__construct($context);

        $this->resultPageFactory = $resultPageFactory;
        $this->configmapVaultService = $configmapVaultService;
        $this->authSession = $authSession;
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


        if (empty($data['configmap_id'])) {
            $data['configmap_id'] = 'new-configmap';
        }

        if (!isset($data['owner'])) {
            $data['owner'] =  $data['owner_fallback'];
        }

        if (empty($data['owner'])) {
            $this->messageManager->addWarningMessage(
                __('Owner field should be not empty.')
            );

            LoggerFacade::error('Choose::execute Owner field should be not empty.');

            return $resultRedirect->setPath('configmap/index/index', [
                'configmap_id' => 'new-configmap',
                'owner' => $this->authSession->getUser()->getUserName(),
            ]);
        }

        $isSecretInMount = $this->configmapVaultService->isSecretInMount($data['owner'], $data['configmap_id']);

        if ($isSecretInMount === false) {
            $this->messageManager->addErrorMessage(
                __('Error while choosing the configmap: configmap %1 did not match with owner %2.', [$data['configmap_id'], $data['owner']])
            );

            LoggerFacade::error('Choose::execute configmap did not match with owner .', ['tenant' => $data['owner'], 'configmap_id' => $data['configmap_id']]);

            return $resultRedirect->setPath('configmap/index/index', [
                'configmap_id' => 'new-configmap',
                'owner' => $data['owner'],
            ]);
        }

        LoggerFacade::debug('Choose action executed', ['data' => $data]);

        $this->_objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class)
            ->setData('configmap_id', $data['configmap_id']);

        $this->_objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class)
            ->setData('owner', $data['owner']);


        if ($data['owner'] == 'xpipe-system') {
           if (ConfigmapHelper::isSuperAdmin($currentUser) === false) {
               $this->messageManager->addErrorMessage(
                   __('Configmap is system reserved and cannot be viewed')
               );

//            $this->messageManager->addErrorMessage(
//                __('Configmap with id %1 is system reserved and cannot be viewed', $data['configmap_id'])
//            );

               LoggerFacade::error('Configmap is system reserved and cannot be viewed.', ['configmap_id' => $data['configmap_id']]);
               return $resultRedirect->setPath('configmap/index/index', ['configmap_id' => 'new-configmap', 'owner' => $data['owner']]);

           }
        }


        return $resultRedirect->setPath('configmap/index/index', ['configmap_id' => $data['configmap_id'], 'owner' => $data['owner']]);
    }



}

