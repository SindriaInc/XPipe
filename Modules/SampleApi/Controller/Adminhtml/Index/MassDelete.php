<?php

namespace Sindria\SampleApi\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\Controller\ResultInterface;
use Magento\Ui\Component\MassAction\Filter;
use Sindria\News\Api\NewsRepositoryInterface;
use Sindria\SampleApi\Model\ResourceModel\External\CollectionFactory;
use Sindria\News\Service\NewsRepository;

class MassDelete extends Action
{

    private Filter $filter;

    private CollectionFactory $collectionFactory;

    public function __construct(Context $context, Filter $filter, CollectionFactory $collectionFactory)
    {
        parent::__construct($context);
        $this->filter = $filter;
        $this->collectionFactory = $collectionFactory;
    }

    public function execute() : ResultInterface
    {


        dd($this->getRequest()->getParams());
//            $collection = $this->filter->getCollection($this->collectionFactory->create());
//            $collectionSize = $collection->getSize();
//            dd($collectionSize, $collection);

//        try {
//            $collection = $this->filter->getCollection($this->collectionFactory->create());
//            $collectionSize = $collection->getSize();
//
//
//            foreach ($collection as $news) {
//                $this->newsRepository->delete($news);
//            }
//            $this->messageManager->addSuccessMessage(__('A total of %1 record(s) have been deleted.', $collectionSize));
//        } catch (\Exception $e) {
//            $this->messageManager->addErrorMessage(__('There was an error while deleting the news!'));
//        }


        $result = $this->resultFactory->create(ResultFactory::TYPE_REDIRECT);

        return $result->setPath('sampleapi/index/index');
    }
}