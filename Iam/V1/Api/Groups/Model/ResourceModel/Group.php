<?php
namespace Iam\Groups\Model\ResourceModel;

use Magento\Framework\Model\AbstractModel;
use Magento\Framework\Model\ResourceModel\Db\AbstractDb;

class Group extends AbstractDb
{
    protected function _construct()
    {
        $this->_init('iam_groups', 'group_id');
    }

    protected function _beforeSave(AbstractModel $object)
    {
        $object->setData('updated_at', date('Y-m-d H:i:s'));
        return parent::_beforeSave($object);
    }
}
