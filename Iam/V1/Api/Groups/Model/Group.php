<?php

namespace Iam\Groups\Model;

use Iam\Groups\Api\Data\GroupInterface;
use Magento\Framework\Model\AbstractModel;

class Group extends AbstractModel implements GroupInterface
{

    private const GROUP_ID = 'group_id';
    private const SLUG = 'slug';
    private const LABEL = 'label';
    private const SHORT = 'short';
    private const CREATED_AT = 'created_at';
    private const UPDATED_AT = 'updated_at';

    protected function _construct()
    {
        $this->_eventPrefix = 'iam_groups';
        $this->_eventObject = 'group';
        $this->_idFieldName = self::GROUP_ID;
        $this->_init(\Iam\Groups\Model\ResourceModel\Group::class);
    }


    public function getGroupId()
    {
        return (int) $this->getData(self::GROUP_ID);
    }

    public function setGroupId($groupId)
    {
        $this->setData(self::GROUP_ID, $groupId);
    }

    public function getSlug()
    {
        return $this->getData(self::SLUG);
    }

    public function setSlug($slug)
    {
        $this->setData(self::SLUG, $slug);
    }

    public function getLabel()
    {
        return $this->getData(self::LABEL);
    }

    public function setLabel($label)
    {
        $this->setData(self::LABEL, $label);
    }

    public function getShort()
    {
        return $this->getData(self::SHORT);
    }

    public function setShort($short)
    {
        $this->setData(self::SHORT, $short);
    }

    public function getCreatedAt()
    {
        return $this->getData(self::CREATED_AT);
    }

    public function setCreatedAt($createdAt)
    {
        $this->setData(self::CREATED_AT, $createdAt);
    }

    public function getUpdatedAt()
    {
        return $this->getData(self::UPDATED_AT);
    }

    public function setUpdatedAt($updatedAt)
    {
        $this->setData(self::UPDATED_AT, $updatedAt);
    }
}
