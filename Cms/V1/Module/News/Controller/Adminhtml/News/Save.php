<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Cms\News\Controller\Adminhtml\News;

use Magento\Backend\App\Action;
use Magento\Framework\App\Action\HttpPostActionInterface;
use Magento\Backend\App\Action\Context;
use Magento\Framework\App\Request\DataPersistorInterface;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\Controller\ResultInterface;
use Magento\Framework\Exception\LocalizedException;
use Magento\Framework\Registry;
use Cms\News\Api\Data\NewsInterfaceFactory;
use Cms\News\Api\NewsRepositoryInterface;

/**
 * Save CMS block action.
 */
class Save extends Action implements HttpPostActionInterface
{

    const ADMIN_RESOURCE = 'Cms_News::add';
    /**
     * @var DataPersistorInterface
     */
    protected $dataPersistor;

    /**
     * @var NewsInterfaceFactory
     */
    private $newsFactory;

    /**
     * @var NewsRepositoryInterface
     */
    private $newsRepository;


    public function __construct(
        Context $context,
        Registry $coreRegistry,
        DataPersistorInterface $dataPersistor,
        NewsInterfaceFactory $newsInterfaceFactory,
        NewsRepositoryInterface $newsRepository
    ) {
        $this->newsFactory = $newsInterfaceFactory;
        $this->newsRepository = $newsRepository;
        $this->dataPersistor = $dataPersistor;
        parent::__construct($context);
    }

    public function execute(): ResultInterface
    {

        $resultRedirect = $this->resultFactory->create(ResultFactory::TYPE_REDIRECT);
        $data = $this->getRequest()->getPostValue()['news'];

        if ($data) {
            if (isset($data['is_active']) && $data['is_active'] === 'true') {
                $data['is_active'] = 1;
            }
            if (empty($data['news_id'])) {
                $data['news_id'] = null;
            }


            $model = $this->newsFactory->create();

            $id = $this->getRequest()->getParam('news_id');
            if ($id) {
                try {
                    $model = $this->newsRepository->getNewsById($id);
                } catch (LocalizedException $e) {
                    $this->messageManager->addErrorMessage(__('This news no longer exists.'));
                    return $resultRedirect->setPath('*/*/');
                }
            }

            $model->setData($data);

            try {
                $this->newsRepository->save($model);
                $this->messageManager->addSuccessMessage(__('You saved the news.'));
                $this->dataPersistor->clear('Cms_news');
                return $resultRedirect->setPath('*/*/');
            } catch (LocalizedException $e) {
                $this->messageManager->addErrorMessage($e->getMessage());
            } catch (\Exception $e) {
                $this->messageManager->addExceptionMessage($e, __('Something went wrong while saving the news.'));
            }

            $this->dataPersistor->set('Cms_news', $data);
            return $resultRedirect->setPath('*/*/edit', ['news_id' => $id]);
        }
        return $resultRedirect->setPath('*/*/');
    }

}
