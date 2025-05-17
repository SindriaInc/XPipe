<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Cms\Faq\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Framework\App\Action\HttpPostActionInterface;
use Magento\Backend\App\Action\Context;
use Magento\Framework\App\Request\DataPersistorInterface;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\Controller\ResultInterface;
use Magento\Framework\Exception\LocalizedException;
use Magento\Framework\Registry;
use Cms\Faq\Api\Data\FaqInterfaceFactory;
use Cms\Faq\Api\FaqRepositoryInterface;
use Psr\Log\LoggerInterface;

/**
 * Save CMS block action.
 */
class Save extends Action implements HttpPostActionInterface
{

    const ADMIN_RESOURCE = 'Cms_Faq::add';
    /**
     * @var DataPersistorInterface
     */
    protected $dataPersistor;

    /**
     * @var FaqInterfaceFactory
     */
    private $faqFactory;

    /**
     * @var FaqRepositoryInterface
     */
    private $faqRepository;

    /**
     * @var LoggerInterface
     */
    protected $logger;




    public function __construct(
        Context $context,
        Registry $coreRegistry,
        DataPersistorInterface $dataPersistor,
        FaqInterfaceFactory $faqInterfaceFactory,
        FaqRepositoryInterface $faqRepository,
        LoggerInterface $logger
    ) {
        $this->faqFactory = $faqInterfaceFactory;
        $this->faqRepository = $faqRepository;
        $this->dataPersistor = $dataPersistor;
        $this->logger = $logger;
        parent::__construct($context);
    }

    public function execute(): ResultInterface
    {

        $resultRedirect = $this->resultFactory->create(ResultFactory::TYPE_REDIRECT);
        $data = $this->getRequest()->getPostValue()['faq'];

        if (count($data) !== 0) {
            if (isset($data['status']) && $data['status'] === 'true') {
                $data['status'] = 1;
            }
            if (empty($data['faq_id'])) {
                $data['faq_id'] = null;
            }


            $model = $this->faqFactory->create();

            $id = (int) $this->getRequest()->getParam('faq_id');
            if ($id) {
                $this->messageManager->addErrorMessage(__('Faq already exists.'));
                return $resultRedirect->setPath('*/*/');
            }

            $model->setData($data);

            try {
                $this->faqRepository->save($model);
                $this->messageManager->addSuccessMessage(__('Faq added successfully.'));
                $this->dataPersistor->clear('cms_faq');

                return $resultRedirect->setPath('*/*/');
            } catch (LocalizedException $e) {
                $this->messageManager->addErrorMessage($e->getMessage());
                $this->dataPersistor->set('cms_faq', $data);
                return $resultRedirect->setPath('*/*/form');
            } catch (\Exception $e) {
                $this->messageManager->addExceptionMessage($e, __('Something went wrong while saving the faq.'));
                $this->dataPersistor->set('cms_faq', $data);
                return $resultRedirect->setPath('*/*/form');
            }
        }

        return $resultRedirect->setPath('*/*/');
    }

}
