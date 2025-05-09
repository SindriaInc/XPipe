<?php
namespace Sindria\Faq\Model\ResourceModel;

use Magento\Framework\Model\AbstractModel;
use Magento\Framework\Model\ResourceModel\Db\AbstractDb;

class Faq extends AbstractDb
{
    protected function _construct()
    {
        $this->_init('sindria_faq', 'faq_id');
    }

    protected function _beforeSave(AbstractModel $object)
    {
        $object->setData('updated_at', date('Y-m-d H:i:s'));
        return parent::_beforeSave($object);
    }
}
