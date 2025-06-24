<?php
/**
 * Copyright Sindria Inc.
 * All rights reserved.
 */


namespace Pipelines\Configmap\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\App\Action\HttpGetActionInterface;
use Magento\Framework\View\Result\Page;
use Magento\Framework\View\Result\PageFactory;
use Magento\Framework\Controller\ResultFactory;
use Pipelines\Configmap\Helper\ConfigmapHelper;

/**
 * Class Index
 */
class Index extends Action implements HttpGetActionInterface
{
    const ADMIN_RESOURCE = 'Pipelines_Configmap::list';

    /**
     * @var PageFactory
     */
    protected $resultPageFactory;

    protected $authSession;

    /**
     * Index constructor.
     *
     * @param Context $context
     * @param PageFactory $resultPageFactory
     */
    public function __construct(
        Context     $context,
        PageFactory $resultPageFactory,
        \Magento\Backend\Model\Auth\Session $authSession
    )
    {
        parent::__construct($context);

        $this->resultPageFactory = $resultPageFactory;
        $this->authSession = $authSession;
    }

    /**
     * Load the page defined in view/adminhtml/layout/exampleadminnewpage_helloworld_index.xml
     *
     *
     */
    public function execute()
    {

        $request = $this->getRequest();
        $configmapId = $request->getParam('configmap_id');
        $owner = $request->getParam('owner');

        if (!$configmapId || !$owner) {

            $defaultConfigmapId = 'new-configmap';
            $owner = $this->authSession->getUser()->getUserName();

            $resultRedirect = $this->resultFactory->create(ResultFactory::TYPE_REDIRECT);

            $resultRedirect->setPath(
                'configmap/index/index',
                [
                    'configmap_id' => $configmapId ?: $defaultConfigmapId,
                    'owner' => $owner,
                    'key' => $request->getParam('key')
                ]
            );

            return $resultRedirect;
        }

        $this->_objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class)
            ->setData('configmap_id', $configmapId);

        $this->_objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class)
            ->setData('owner', $owner);

        $resultPage = $this->resultPageFactory->create();
        $resultPage->getConfig()->getTitle()->prepend(__('Configmap'));

        return $resultPage;
    }
}

