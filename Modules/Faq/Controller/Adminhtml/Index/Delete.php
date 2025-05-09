<?php

namespace Sindria\Faq\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\Controller\ResultInterface;
use Sindria\Faq\Api\FaqRepositoryInterface;
use Sindria\Faq\Model\ResourceModel\Faq\CollectionFactory;

class Delete extends Action
{

//    const ADMIN_RESOURCE = 'Sindria_Faq::delete';
    private CollectionFactory $collectionFactory;

    private FaqRepositoryInterface $faqRepository;

    public function __construct(Context $context, CollectionFactory $collectionFactory, FaqRepositoryInterface $faqRepository)
    {
        parent::__construct($context);
        $this->collectionFactory = $collectionFactory;
        $this->faqRepository = $faqRepository;
    }

    public function execute() : ResultInterface
    {

        $faqId = $this->getRequest()->getParam('faq_id', 0);

        $result = $this->resultFactory->create(ResultFactory::TYPE_REDIRECT);

        if (!$faqId) {
            $this->messageManager->addWarningMessage(__('Faq not found.'));
            return $result->setPath('faq/index/index');
        }

        try {

            $faq = $this->faqRepository->getFaqById($faqId);
            $this->faqRepository->delete($faq);

            $this->messageManager->addSuccessMessage(__('Faq has been deleted.'));
        } catch (\Exception $e) {
            $this->messageManager->addErrorMessage(__('There was an error while deleting the faq!'));
        }

        return $result->setPath('faq/index/index');
    }
}