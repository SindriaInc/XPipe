<?php

namespace Iam\Groups\Api\Data;

interface UserGroupInterface
{
    public function getUserGroupId();

    public function setUserGroupId($userGroupId);

    public function getUsername();
    public function setUsername($username);

    public function getGroupId();

    public function setGroupId($groupId);


}