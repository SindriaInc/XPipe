<?php
/**
 * Copyright [first year code created] Adobe
 * All rights reserved.
 */

namespace Academy\SampleApi\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\App\Action\HttpGetActionInterface;
use Magento\Framework\View\Result\Page;
use Magento\Framework\View\Result\PageFactory;
use Academy\SampleApi\Ui\Component\Form\DataProvider;

/**
 * Class Index
 */
class Form extends Action implements HttpGetActionInterface
{

    protected PageFactory $resultPageFactory;

    protected DataProvider $dataProvider;

    public function __construct(
        Context $context,
        PageFactory $resultPageFactory,
        DataProvider $dataProvider
    ) {

        parent::__construct($context);

        $this->dataProvider = $dataProvider;
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

        $data = current($this->dataProvider->getData());

        $resultPage->setActiveMenu('Academy_SampleApi::sampleapi');
        $resultPage->addBreadcrumb(__('SampleApi'), __('SampleApi'));
        $resultPage->addBreadcrumb(
            $data ? $data['data']['name'] : __('Add'),  $data ? $data['data']['name'] : __('Add'));
        $resultPage->getConfig()->getTitle()->prepend( $data ? $data['data']['name'] : __('Add'));
        return $resultPage;
    }
}

