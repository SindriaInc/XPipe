<?php
namespace Iam\Groups\Model\ResourceModel;

use Magento\Framework\Model\AbstractModel;
use Magento\Framework\Model\ResourceModel\Db\AbstractDb;

class UserGroup extends AbstractDb
{
    protected function _construct()
    {
        $this->_init('iam_user_group', 'user_group_id');
    }

}
