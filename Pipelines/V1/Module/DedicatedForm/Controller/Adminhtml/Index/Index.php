<?php
/**
 * Copyright Sindria Inc.
 * All rights reserved.
 */


namespace Pipelines\DedicatedForm\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\App\Action\HttpGetActionInterface;
use Magento\Framework\View\Result\Page;
use Magento\Framework\View\Result\PageFactory;

/**
 * Class Index
 */
class Index extends Action implements HttpGetActionInterface
{
//    const ADMIN_RESOURCE = 'Pipelines_TemplateStore::selectincluded';

    /**
     * @var PageFactory
     */
    protected $resultPageFactory;

    protected $_authSession;

    /**
     * Index constructor.
     *
     * @param Context $context
     * @param PageFactory $resultPageFactory
     */
    public function __construct(
        Context     $context,
        PageFactory $resultPageFactory,
        \Magento\Backend\Model\Auth\Session $_authSession
    )
    {
        parent::__construct($context);

        $this->resultPageFactory = $resultPageFactory;
        $this->_authSession = $_authSession;
    }

    /**
     * Load the page defined in view/adminhtml/layout/exampleadminnewpage_helloworld_index.xml
     *
     * @return Page
     */
    public function execute()
    {

        $ticketId = $this->getRequest()->getParam('ticket_id');
        $this->_objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class)
            ->setData('ticket_id', $ticketId);

        $username = $this->_authSession->getUser()->getUserName();

        $this->_objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class)
            ->setData('username', $username);


        $resultPage = $this->resultPageFactory->create();
        $resultPage->getConfig()->getTitle()->prepend(__('Request Dedicated Pipeline'));
        return $resultPage;
    }
}

