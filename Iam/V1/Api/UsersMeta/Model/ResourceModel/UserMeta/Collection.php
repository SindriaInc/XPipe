<?php
namespace Iam\UsersMeta\Model\ResourceModel\UserMeta;

use Magento\Framework\Model\ResourceModel\Db\Collection\AbstractCollection;
use Iam\UsersMeta\Model\UserMeta as UserMetaModel;
use Iam\UsersMeta\Model\ResourceModel\UserMeta as UserMetaResource;

class Collection extends AbstractCollection
{
    protected function _construct()
    {
        $this->_init(UserMetaModel::class, UserMetaResource::class);
    }
}
