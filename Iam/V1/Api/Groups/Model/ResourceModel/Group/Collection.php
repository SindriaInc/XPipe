<?php
namespace Iam\Groups\Model\ResourceModel\Group;

use Magento\Framework\Model\ResourceModel\Db\Collection\AbstractCollection;
use Iam\Groups\Model\Group as GroupModel;
use Iam\Groups\Model\ResourceModel\Group as GroupResource;

class Collection extends AbstractCollection
{
    protected function _construct()
    {
        $this->_init(GroupModel::class, GroupResource::class);
    }
}
