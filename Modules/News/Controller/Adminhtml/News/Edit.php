<?php
/**
 * Copyright [first year code created] Adobe
 * All rights reserved.
 */

namespace Sindria\News\Controller\Adminhtml\News;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\App\Action\HttpGetActionInterface;
use Magento\Framework\Exception\NoSuchEntityException;
use Magento\Framework\View\Result\Page;
use Magento\Framework\View\Result\PageFactory;
use PHPUnit\Exception;
use Sindria\News\Api\NewsRepositoryInterface;

/**
 * Class Index
 */
class Edit extends Action implements HttpGetActionInterface
{

    protected PageFactory $resultPageFactory;

    private NewsRepositoryInterface $newsRepository;


    public function __construct(
        Context $context,
        PageFactory $resultPageFactory,
        NewsRepositoryInterface $newsRepository

    ) {
        parent::__construct($context);

        $this->resultPageFactory = $resultPageFactory;
        $this->newsRepository = $newsRepository;
    }

    /**
     * Load the page defined in view/adminhtml/layout/exampleadminnewpage_helloworld_index.xml
     *
     * @return Page
     */
    public function execute()
    {
        $resultPage = $this->resultPageFactory->create();

        $newsId = (int)$this->getRequest()->getParam('news_id');

        try {
            $news = $this->newsRepository->getNewsById($newsId);
        } catch (Exception $e) {
            $this->messageManager->addErrorMessage(__('This news no longer exists.'));
        }

        $resultPage->setActiveMenu('Sindria_News::news');
        $resultPage->addBreadcrumb(__('News'), __('News'));
        $resultPage->addBreadcrumb(
           $news->getNewsId() ? $news->getTitle() : __('Add News'), $news->getNewsId() ? $news->getTitle() : __('Add News'));
        $resultPage->getConfig()->getTitle()->prepend($news->getNewsId() ? $news->getTitle() : __('Add News'));
        return $resultPage;
    }
}

