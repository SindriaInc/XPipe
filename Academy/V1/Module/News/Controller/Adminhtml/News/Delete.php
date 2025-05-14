<?php

namespace Academy\News\Controller\Adminhtml\News;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\Controller\ResultInterface;
use Academy\News\Api\NewsRepositoryInterface;
use Academy\News\Model\ResourceModel\News\CollectionFactory;

class Delete extends Action
{

    const ADMIN_RESOURCE = 'Academy_News::delete';
    private CollectionFactory $collectionFactory;

    private NewsRepositoryInterface $newsRepository;
    public function __construct(Context $context, CollectionFactory $collectionFactory, NewsRepositoryInterface $newsRepository)
    {
        parent::__construct($context);
        $this->collectionFactory = $collectionFactory;
        $this->newsRepository = $newsRepository;
    }

    public function execute() : ResultInterface
    {

        $newsId = $this->getRequest()->getParam('news_id', 0);

        $result = $this->resultFactory->create(ResultFactory::TYPE_REDIRECT);

        if (!$newsId) {
            $this->messageManager->addWarningMessage(__('News not found.'));
            return $result->setPath('news/news/index');
        }

        try {

            $news = $this->newsRepository->getNewsById($newsId);
            $this->newsRepository->delete($news);

            $this->messageManager->addSuccessMessage(__('News has been deleted.'));
        } catch (\Exception $e) {
            $this->messageManager->addErrorMessage(__('There was an error while deleting the news!'));
        }

        return $result->setPath('news/news/index');
    }
}