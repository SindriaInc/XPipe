<?php
/**
 * Copyright Sindria Inc.
 * All rights reserved.
 */


namespace Pipelines\Configmap\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\App\Action\HttpGetActionInterface;
use Magento\Framework\App\Action\HttpPostActionInterface;
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

        dd($this->getRequest()->getPostValue());

        $configmapId = (int)$this->getRequest()->getPostValue()['configmap'];

        $this->_objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class)
            ->setData('configmap_id', $configmapId);



        $resultPage = $this->resultPageFactory->create();
        $resultPage->getConfig()->getTitle()->prepend(__('Configmap'));
        return $resultPage;
    }
}

