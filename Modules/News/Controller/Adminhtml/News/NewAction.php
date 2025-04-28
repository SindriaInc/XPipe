<?php

namespace Sindria\News\Controller\Adminhtml\News;

use Magento\Backend\App\Action;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\Controller\ResultInterface;

class NewAction extends Action
{

    const ADMIN_RESOURCE = 'Sindria_News::news';

    public function execute() : ResultInterface
    {
        return $this->resultFactory->create(ResultFactory::TYPE_FORWARD)->forward('edit');
    }
}