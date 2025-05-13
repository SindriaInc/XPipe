<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Sindria\Faq\Controller\Adminhtml\Index;

use Magento\Backend\App\Action;
use Magento\Framework\App\Action\HttpPostActionInterface;
use Magento\Backend\App\Action\Context;
use Magento\Framework\App\Request\DataPersistorInterface;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\Controller\ResultInterface;
use Magento\Framework\Exception\LocalizedException;
use Magento\Framework\Registry;
use Sindria\Faq\Api\Data\FaqInterfaceFactory;
use Sindria\Faq\Api\FaqRepositoryInterface;
use Psr\Log\LoggerInterface;

/**
 * Save CMS block action.
 */
class Save extends Action implements HttpPostActionInterface
{

    const ADMIN_RESOURCE = 'Sindria_Faq::add';
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
        dd($data);


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
                $this->dataPersistor->clear('sindria_faq');

                return $resultRedirect->setPath('*/*/');
            } catch (LocalizedException $e) {
                $this->messageManager->addErrorMessage($e->getMessage());
                $this->dataPersistor->set('sindria_faq', $data);
                return $resultRedirect->setPath('*/*/form');
            } catch (\Exception $e) {
                $this->messageManager->addExceptionMessage($e, __('Something went wrong while saving the faq.'));
                $this->dataPersistor->set('sindria_faq', $data);
                return $resultRedirect->setPath('*/*/form');
            }
        }

        return $resultRedirect->setPath('*/*/');
    }

}
