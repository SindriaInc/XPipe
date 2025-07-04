<?php

namespace Academy\News\Controller\Adminhtml\News;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\Controller\ResultInterface;
use Magento\Ui\Component\MassAction\Filter;
use Academy\News\Api\NewsRepositoryInterface;
use Academy\News\Model\ResourceModel\News\CollectionFactory;
use Academy\News\Service\NewsRepository;

class MassEnable extends Action
{

    const ADMIN_RESOURCE = 'Academy_News::edit';

    private Filter $filter;

    private CollectionFactory $collectionFactory;

    private NewsRepositoryInterface $newsRepository;
    public function __construct(Context $context, Filter $filter, CollectionFactory $collectionFactory, NewsRepositoryInterface $newsRepository)
    {
        parent::__construct($context);
        $this->filter = $filter;
        $this->collectionFactory = $collectionFactory;
        $this->newsRepository = $newsRepository;
    }

    public function execute() : ResultInterface
    {

        try {
            $collection = $this->filter->getCollection($this->collectionFactory->create());
            $collectionSize = $collection->getSize();


            foreach ($collection as $news) {
                $news->setIsActive(1);
                $this->newsRepository->save($news);
            }
            $this->messageManager->addSuccessMessage(__('A total of %1 record(s) have been enabled.', $collectionSize));
        } catch (\Exception $e) {
            $this->messageManager->addErrorMessage(__('There was an error while enabling the news!'));
        }


        $result = $this->resultFactory->create(ResultFactory::TYPE_REDIRECT);

        return $result->setPath('news/news/index');
    }
}