<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Sindria\Faq\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Framework\App\Action\HttpPostActionInterface;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\Controller\ResultInterface;
use Magento\Framework\Exception\LocalizedException;
use Magento\Framework\App\Request\DataPersistorInterface;
use Sindria\Faq\Api\Data\FaqInterfaceFactory;
use Sindria\Faq\Api\FaqRepositoryInterface;

class Save extends Action implements HttpPostActionInterface
{
    protected $dataPersistor;
    private $faqFactory;
    private $faqRepository;

    public function __construct(
        Action\Context $context,
        DataPersistorInterface $dataPersistor,
        FaqInterfaceFactory $faqFactory,
        FaqRepositoryInterface $faqRepository
    ) {
        $this->faqFactory = $faqFactory;
        $this->faqRepository = $faqRepository;
        $this->dataPersistor = $dataPersistor;
        parent::__construct($context);
    }

    public function execute(): ResultInterface
    {
        $resultRedirect = $this->resultFactory->create(ResultFactory::TYPE_REDIRECT);
        $data = $this->getRequest()->getPostValue()['faq'] ?? [];

        if (!empty($data)) {
            if (isset($data['status']) && $data['status'] === 'true') {
                $data['status'] = 1;
            }

            if (!empty($data['faq_id'])) {
                $this->messageManager->addErrorMessage(__('Cannot create FAQ: ID must not be provided.'));
                return $resultRedirect->setPath('*/*/');
            }

            $model = $this->faqFactory->create();
            $model->setData($data);

            try {
                $this->faqRepository->save($model);
                $this->messageManager->addSuccessMessage(__('You created the FAQ.'));
                $this->dataPersistor->clear('sindria_faq');
                return $resultRedirect->setPath('*/*/');
            } catch (LocalizedException $e) {
                $this->messageManager->addErrorMessage($e->getMessage());
            } catch (\Exception $e) {
                $this->messageManager->addExceptionMessage($e, __('Something went wrong while creating the FAQ.'));
            }

            $this->dataPersistor->set('sindria_faq', $data);
            return $resultRedirect->setPath('*/*/form');
        }

        return $resultRedirect->setPath('*/*/');
    }
}
