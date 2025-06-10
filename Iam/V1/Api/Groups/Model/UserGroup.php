<?php

namespace Iam\Groups\Model;

use Iam\Groups\Api\Data\UserGroupInterface;
use Magento\Framework\Model\AbstractModel;

class UserGroup extends AbstractModel implements UserGroupInterface
{

    private const USER_GROUP_ID = 'user_group_id';

    private const USERNAME = 'username';
    private const GROUP_ID = 'group_id';


    protected function _construct()
    {
        $this->_eventPrefix = 'iam_user_group';
        $this->_eventObject = 'user_group';
        $this->_idFieldName = self::USER_GROUP_ID;
        $this->_init(\Iam\Groups\Model\ResourceModel\UserGroup::class);
    }

    public function getUserGroupId()
    {
        return $this->getData(self::USER_GROUP_ID);
    }

    public function setUserGroupId($userGroupId)
    {
        $this->setData(self::USER_GROUP_ID, $userGroupId);
    }

    public function getUsername()
    {
        return $this->getData(self::USERNAME);
    }

    public function setUsername($username)
    {
        $this->setData(self::USERNAME, $username);
    }

    public function getGroupId()
    {
        return (int) $this->getData(self::GROUP_ID);
    }

    public function setGroupId($groupId)
    {
        $this->setData(self::GROUP_ID, $groupId);
    }
}
