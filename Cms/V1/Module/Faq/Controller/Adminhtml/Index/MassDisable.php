<?php

namespace Cms\Faq\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\Controller\ResultInterface;
use Magento\Ui\Component\MassAction\Filter;
use Cms\Faq\Api\FaqRepositoryInterface;
use Cms\Faq\Model\ResourceModel\Faq\CollectionFactory;
use Cms\Faq\Service\FaqRepository;

class MassDisable extends Action
{

    const ADMIN_RESOURCE = 'Cms_Faq::edit';
    private Filter $filter;

    private CollectionFactory $collectionFactory;

    private FaqRepositoryInterface $faqRepository;
    public function __construct(Context $context, Filter $filter, CollectionFactory $collectionFactory, FaqRepositoryInterface $faqRepository)
    {
        parent::__construct($context);
        $this->filter = $filter;
        $this->collectionFactory = $collectionFactory;
        $this->faqRepository = $faqRepository;
    }

    public function execute() : ResultInterface
    {

        try {
            $collection = $this->filter->getCollection($this->collectionFactory->create());
            $collectionSize = $collection->getSize();


            foreach ($collection as $faq) {
                $faq->setStatus(0);
                $this->faqRepository->save($faq);
            }
            $this->messageManager->addSuccessMessage(__('A total of %1 record(s) have been disabled.', $collectionSize));
        } catch (\Exception $e) {
            $this->messageManager->addErrorMessage(__('There was an error while disabling the faq!'));
        }


        $result = $this->resultFactory->create(ResultFactory::TYPE_REDIRECT);

        return $result->setPath('faq/index/index');
    }
}