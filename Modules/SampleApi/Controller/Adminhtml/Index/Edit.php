<?php
/**
 * Copyright [first year code created] Adobe
 * All rights reserved.
 */

namespace Sindria\SampleApi\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\App\Action\HttpGetActionInterface;
use Magento\Framework\View\Result\Page;
use Magento\Framework\View\Result\PageFactory;
use PHPUnit\Exception;
use Sindria\News\Api\Data\NewsInterfaceFactory;
use Sindria\News\Api\NewsRepositoryInterface;

/**
 * Class Index
 */
class Edit extends Action implements HttpGetActionInterface
{


    protected PageFactory $resultPageFactory;

    private NewsRepositoryInterface $newsRepository;

    private NewsInterfaceFactory $newsFactory;


    public function __construct(
        Context $context,
        PageFactory $resultPageFactory,
        NewsRepositoryInterface $newsRepository,
        NewsInterfaceFactory $newsFactory

    ) {
        parent::__construct($context);

        $this->resultPageFactory = $resultPageFactory;
        $this->newsRepository = $newsRepository;
        $this->newsFactory = $newsFactory;
    }

    /**
     * Load the page defined in view/adminhtml/layout/exampleadminnewpage_helloworld_index.xml
     *
     * @return Page
     */
    public function execute()
    {
        $resultPage = $this->resultPageFactory->create();

        $entryId = (int)$this->getRequest()->getParam('id');

        if ($entryId) {
            try {

//                $client = new HttpClient();
//                $client->setHeaders(['Content-Type' => 'application/json']);
//                $client->setOptions(['timeout' => 10]);

                //call api with id and edit
//                $news = $this->newsRepository->getNewsById($newsId);
            } catch (Exception $e) {
                $this->messageManager->addErrorMessage(__('This entry no longer exists.'));
            }
        } else {
//            $news = $this->newsFactory->create();
            //call api and create
        }



        $resultPage->setActiveMenu('Sindria_SampleApi::sampleapi');
        $resultPage->addBreadcrumb(__('SampleApi'), __('SampleApi'));
        $resultPage->addBreadcrumb(
            __('Add'),  __('Add'));
        $resultPage->getConfig()->getTitle()->prepend( __('Add'));
        return $resultPage;
    }
}

