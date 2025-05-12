<?php
/**
 * Copyright [first year code created] Adobe
 * All rights reserved.
 */

namespace Sindria\Faq\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\App\Action\HttpGetActionInterface;
use Magento\Framework\Exception\NoSuchEntityException;
use Magento\Framework\View\Result\Page;
use Magento\Framework\View\Result\PageFactory;
use PHPUnit\Exception;
use Sindria\Faq\Api\Data\FaqInterfaceFactory;
use Sindria\Faq\Api\FaqRepositoryInterface;
use Sindria\Faq\Model\Faq;

/**
 * Class Index
 */
class Form extends Action implements HttpGetActionInterface
{

    const ADMIN_RESOURCE = 'Sindria_Faq::edit';

    protected PageFactory $resultPageFactory;

    private FaqRepositoryInterface $faqRepository;

    private FaqInterfaceFactory $faqFactory;


    public function __construct(
        Context $context,
        PageFactory $resultPageFactory,
        FaqRepositoryInterface $faqRepository,
        FaqInterfaceFactory $faqFactory

    ) {
        parent::__construct($context);

        $this->resultPageFactory = $resultPageFactory;
        $this->faqRepository = $faqRepository;
        $this->faqFactory = $faqFactory;
    }

    /**
     * Load the page defined in view/adminhtml/layout/exampleadminnewpage_helloworld_index.xml
     *
     * @return Page
     */
    public function execute()
    {
        $resultPage = $this->resultPageFactory->create();

        $faqId = (int)$this->getRequest()->getParam('faq_id');

        if ($faqId) {
            try {
                $faq = $this->faqRepository->getFaqById($faqId);
            } catch (Exception $e) {
                $this->messageManager->addErrorMessage(__('This faq no longer exists.'));
            }
        } else {
//            $faq = $this->faqFactory->create();
            $faq = $this->_objectManager->create(Faq::class);
        }


        $resultPage->setActiveMenu('Sindria_Faq::faq');
        $resultPage->addBreadcrumb(__('Faq'), __('Faq'));
        $resultPage->addBreadcrumb(
           $faq->getFaqId() ? $faq->getQuestion() : __('Add Faq'), $faq->getFaqId() ? $faq->getQuestion() : __('Add Faq'));
        $resultPage->getConfig()->getTitle()->prepend($faq->getFaqId() ? $faq->getQuestion() : __('Add Faq'));
        return $resultPage;
    }
}

