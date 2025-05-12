<?php

namespace Sindria\Faq\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\Controller\ResultInterface;
use Sindria\Faq\Api\FaqRepositoryInterface;
use Sindria\Faq\Model\ResourceModel\Faq\CollectionFactory;

class InlineEdit extends Action
{

    const ADMIN_RESOURCE = 'Sindria_Faq::edit';

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

        $result = $this->resultFactory->create(ResultFactory::TYPE_JSON);
        $items = $this->getRequest()->getParam('items');
        $messages = [];
        $error = false;



        if (!count($items)) {
            $messages[] = __('Invalid input.');
            $error = true;
        } else {

            foreach (array_keys($items) as $faqId) {
                try {
                    $faq = $this->faqRepository->getFaqById($faqId);
                    $faq->setData(array_merge($faq->getData(), $items[$faqId]));
                    $this->faqRepository->save($faq);
                } catch (\Exception $e) {
                    $messages[] ='[Faq ID: ' . $faqId . '] ' .   $e->getMessage();
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