<?php

namespace Sindria\Faq\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\Controller\ResultInterface;
use Magento\Ui\Component\MassAction\Filter;
use Sindria\Faq\Api\FaqRepositoryInterface;
use Sindria\Faq\Model\ResourceModel\Faq\CollectionFactory;
use Sindria\Faq\Service\FaqRepository;

class MassEnable extends Action
{

    const ADMIN_RESOURCE = 'Sindria_Faq::edit';

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
                $faq->setStatus(1);
                $this->faqRepository->save($faq);
            }
            $this->messageManager->addSuccessMessage(__('A total of %1 record(s) have been enabled.', $collectionSize));
        } catch (\Exception $e) {
            $this->messageManager->addErrorMessage(__('There was an error while enabling the faq!'));
        }


        $result = $this->resultFactory->create(ResultFactory::TYPE_REDIRECT);

        return $result->setPath('faq/index/index');
    }
}