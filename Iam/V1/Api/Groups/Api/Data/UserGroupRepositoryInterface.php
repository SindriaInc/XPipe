<?php

namespace Iam\Groups\Api\Data;

use Iam\Groups\Model\ResourceModel\UserGroup\Collection;

interface UserGroupRepositoryInterface
{
    public function attach(string $username, int $groupId) : void;

    public function update(array $existingData, array $payload) : UserGroupInterface;

    public function delete(string $slug) : UserGroupInterface;

    public function getUserGroupById(int $id) : UserGroupInterface;
    public function find(string $slug) : UserGroupInterface;

    public function all() : Collection;
}