<?php
/**
 * Copyright Sindria Inc.
 * All rights reserved.
 */


namespace Pipelines\Configmap\Controller\Adminhtml\Index;

use Core\Logger\Facade\LoggerFacade;
use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\App\Action\HttpGetActionInterface;
use Magento\Framework\App\Action\HttpPostActionInterface;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\View\Result\Page;
use Magento\Framework\View\Result\PageFactory;

/**
 * Class Index
 */
class Choose extends Action implements HttpPostActionInterface
{
    const ADMIN_RESOURCE = 'Pipelines_Configmap::configmap';

    /**
     * @var PageFactory
     */
    protected $resultPageFactory;

    /**
     * Index constructor.
     *
     * @param Context $context
     * @param PageFactory $resultPageFactory
     */
    public function __construct(
        Context     $context,
        PageFactory $resultPageFactory
    )
    {
        parent::__construct($context);

        $this->resultPageFactory = $resultPageFactory;
    }

    /**
     * Load the page defined in view/adminhtml/layout/exampleadminnewpage_helloworld_index.xml
     *
     * @return Page
     */
    public function execute()
    {
        $resultRedirect = $this->resultFactory->create(ResultFactory::TYPE_REDIRECT);

        $data = $this->getRequest()->getPostValue();

        LoggerFacade::debug('Choose action executed', ['data' => $data]);

        $this->_objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class)
            ->setData('configmap_id', $data['configmap_id']);

        $this->_objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class)
            ->setData('owner', $data['owner']);


//        if ($data['owner'] !== 'luca.pitzoi' || $data['owner'] !== 'dorje.curreli') {
//            if ($data['configmap_id'] !== 'xpipe-iaas' || $data['configmap_id'] !== 'xpipe-saas') {
//                $this->messageManager->addErrorMessage(
//                    __('Configmap with id %1 is system reserved and cannot be viewed', $data['configmap_id'])
//                );
//                LoggerFacade::error('Configmap is system reserved and cannot be viewed.', ['configmap_id' => $data['configmap_id']]);
//                return $resultRedirect->setPath('configmap/index/index', ['configmap_id' => 'new-configmap', 'owner' => $data['owner']]);
//            }
//        }

        return $resultRedirect->setPath('configmap/index/index', ['configmap_id' => $data['configmap_id'], 'owner' => $data['owner']]);
    }
}

