<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Sindria\Faq\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Backend\App\Action\Context;
use Magento\Framework\App\Action\HttpPostActionInterface;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\Controller\ResultInterface;
use Magento\Framework\Exception\LocalizedException;
use Magento\Framework\App\Request\DataPersistorInterface;
use Magento\Framework\Registry;
use Sindria\Faq\Api\Data\FaqInterfaceFactory;
use Sindria\Faq\Api\FaqRepositoryInterface;

class Edit extends Action implements HttpPostActionInterface
{


    const ADMIN_RESOURCE = 'Sindria_Faq::edit';

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


    public function __construct(
        Context $context,
        Registry $coreRegistry,
        DataPersistorInterface $dataPersistor,
        FaqInterfaceFactory $faqInterfaceFactory,
        FaqRepositoryInterface $faqRepository
    ) {
        $this->faqFactory = $faqInterfaceFactory;
        $this->faqRepository = $faqRepository;
        $this->dataPersistor = $dataPersistor;
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


            $id = (int) $this->getRequest()->getParam('faq_id');

            $model = $this->faqFactory->create();

            if ($id) {
                try {
                    $model = $this->faqRepository->getFaqById($id);
                } catch (LocalizedException $e) {
                    $this->messageManager->addErrorMessage(__('This faq no longer exists.'));
                    return $resultRedirect->setPath('*/*/');
                }
            } else {
                $this->messageManager->addErrorMessage(__('This faq no longer exists.'));
                return $resultRedirect->setPath('*/*/');
            }


            $model->setData($data);


            try {
                $this->faqRepository->save($model);
                $this->messageManager->addSuccessMessage(__('Faq edited successfully.'));
                $this->dataPersistor->clear('sindria_faq');
                $redirect = $resultRedirect->setPath('*/*/');
                return $redirect;
            } catch (LocalizedException $e) {
                $this->messageManager->addErrorMessage($e->getMessage());
                $this->dataPersistor->set('sindria_faq', $data);
                return $resultRedirect->setPath('*/*/form', ['faq_id' => $id]);
            } catch (\Exception $e) {
                $this->messageManager->addExceptionMessage($e, __('Something went wrong while saving the faq.'));
                $this->dataPersistor->set('sindria_faq', $data);
                return $resultRedirect->setPath('*/*/form', ['faq_id' => $id]);
            }

        }

        return $resultRedirect->setPath('*/*/');
    }
}
