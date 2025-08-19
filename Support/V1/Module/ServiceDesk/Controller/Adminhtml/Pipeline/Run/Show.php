<?php
/**
 * Copyright Sindria Inc.
 * All rights reserved.
 */


namespace Support\ServiceDesk\Controller\Adminhtml\Pipeline\Run;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\App\Action\HttpGetActionInterface;
use Magento\Framework\View\Result\Page;
use Magento\Framework\View\Result\PageFactory;

/**
 * Class Index
 */
class Show extends Action implements HttpGetActionInterface
{
    const ADMIN_RESOURCE = 'Support_ServiceDesk::showlogs';

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
        $resultPage = $this->resultPageFactory->create();
        //$resultPage->setActiveMenu('Support_ServiceDesk::logs');
        $resultPage->getConfig()->getTitle()->prepend(__('Service Desk Run Logs'));

        return $resultPage;
    }
}

