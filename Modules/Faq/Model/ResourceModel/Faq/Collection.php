<?php
namespace Sindria\Faq\Model\ResourceModel\Faq;

use Magento\Framework\Model\ResourceModel\Db\Collection\AbstractCollection;
use Sindria\Faq\Model\Faq as FaqModel;
use Sindria\Faq\Model\ResourceModel\Faq as FaqResource;

class Collection extends AbstractCollection
{
    protected function _construct()
    {
        $this->_init(FaqModel::class, FaqResource::class);
    }
}
