<?php
/**
 * Copyright [first year code created] Adobe
 * All rights reserved.
 */

namespace Cms\News\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\App\Action\HttpGetActionInterface;
use Magento\Framework\Exception\NoSuchEntityException;
use Magento\Framework\View\Result\Page;
use Magento\Framework\View\Result\PageFactory;
use PHPUnit\Exception;
use Cms\News\Api\Data\NewsInterfaceFactory;
use Cms\News\Api\NewsRepositoryInterface;

/**
 * Class Index
 */
class Edit extends Action implements HttpGetActionInterface
{

    const ADMIN_RESOURCE = 'Cms_News::edit';

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

        $newsId = (int)$this->getRequest()->getParam('news_id');

        if ($newsId) {
            try {
                $news = $this->newsRepository->getNewsById($newsId);
            } catch (Exception $e) {
                $this->messageManager->addErrorMessage(__('This news no longer exists.'));
            }
        } else {
            $news = $this->newsFactory->create();
        }



        $resultPage->setActiveMenu('Cms_News::news');
        $resultPage->addBreadcrumb(__('News'), __('News'));
        $resultPage->addBreadcrumb(
           $news->getNewsId() ? $news->getTitle() : __('Add News'), $news->getNewsId() ? $news->getTitle() : __('Add News'));
        $resultPage->getConfig()->getTitle()->prepend($news->getNewsId() ? $news->getTitle() : __('Add News'));
        return $resultPage;
    }
}

