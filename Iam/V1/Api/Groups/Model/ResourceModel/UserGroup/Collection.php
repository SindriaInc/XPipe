<?php
namespace Iam\Groups\Model\ResourceModel\UserGroup;

use Magento\Framework\Model\ResourceModel\Db\Collection\AbstractCollection;
use Iam\Groups\Model\UserGroup as UserGroupModel;
use Iam\Groups\Model\ResourceModel\UserGroup as UserGroupResource;

class Collection extends AbstractCollection
{
    protected function _construct()
    {
        $this->_init(UserGroupModel::class, UserGroupResource::class);
    }
}
