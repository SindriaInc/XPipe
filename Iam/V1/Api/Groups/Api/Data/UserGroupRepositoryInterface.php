<?php

namespace Iam\Groups\Api\Data;

use Iam\Groups\Model\ResourceModel\UserGroup\Collection;
use Magento\Framework\DataObject;

interface UserGroupRepositoryInterface
{
    public function attach(string $username, int $groupId) : void;
    public function detach(string $username, int $groupId) : void;

    public function attachedUsers(string $groupSlug) : array;



    public function getUserGroupByUsernameAndGroupId(string $username, int $groupId);


}