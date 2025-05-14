<?php

namespace Core\News\Controller\Adminhtml\News;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\Controller\ResultInterface;
use Sindria\News\Api\NewsRepositoryInterface;
use Sindria\News\Model\ResourceModel\News\CollectionFactory;

class InlineEdit extends Action
{

    const ADMIN_RESOURCE = 'Core_News::edit';

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

        $result = $this->resultFactory->create(ResultFactory::TYPE_JSON);
        $items = $this->getRequest()->getParam('items');
        $messages = [];
        $error = false;



        if (!count($items)) {
            $messages[] = __('Invalid input.');
            $error = true;
        } else {

            foreach (array_keys($items) as $newsId) {
                try {
                    $news = $this->newsRepository->getNewsById($newsId);
                    $news->setData(array_merge($news->getData(), $items[$newsId]));
                    $this->newsRepository->save($news);
                } catch (\Exception $e) {
                    $messages[] ='[News ID: ' . $newsId . '] ' .   $e->getMessage();
                    $error = true;
                }
            }


        }



        return $result->setData([
            'messages' => $messages,
            'error' => $error
        ]);
    }
}