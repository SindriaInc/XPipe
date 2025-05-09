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
use Sindria\Faq\Api\FaqRepositoryInterface;

class Edit extends Action implements HttpPostActionInterface
{
    protected $dataPersistor;
    private $faqRepository;

    public function __construct(
        Action\Context $context,
        DataPersistorInterface $dataPersistor,
        FaqRepositoryInterface $faqRepository
    ) {
        $this->faqRepository = $faqRepository;
        $this->dataPersistor = $dataPersistor;
        parent::__construct($context);
    }

    public function execute(): ResultInterface
    {
        $resultRedirect = $this->resultFactory->create(ResultFactory::TYPE_REDIRECT);
        $data = $this->getRequest()->getPostValue()['faq'] ?? [];
        $id = $data['faq_id'] ?? null;

        if (!empty($data) && $id) {
            if (isset($data['status']) && $data['status'] === 'true') {
                $data['status'] = 1;
            }

            try {
                $model = $this->faqRepository->getFaqById($id);
                $model->setData($data);
                $this->faqRepository->save($model);
                $this->messageManager->addSuccessMessage(__('You updated the FAQ.'));
                $this->dataPersistor->clear('sindria_faq');
                return $resultRedirect->setPath('*/*/');
            } catch (LocalizedException $e) {
                $this->messageManager->addErrorMessage($e->getMessage());
            } catch (\Exception $e) {
                $this->messageManager->addExceptionMessage($e, __('Something went wrong while editing the FAQ.'));
            }

            $this->dataPersistor->set('sindria_faq', $data);
            return $resultRedirect->setPath('*/*/form', ['faq_id' => $id]);
        }

        $this->messageManager->addErrorMessage(__('Invalid FAQ data or missing ID.'));
        return $resultRedirect->setPath('*/*/');
    }
}
