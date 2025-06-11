<?php

namespace Iam\Groups\Api\Data;

use Iam\Groups\Model\ResourceModel\Group\Collection;

interface GroupRepositoryInterface
{
    public function save(array $payload) : GroupInterface;

    public function update(array $existingData, array $payload) : GroupInterface;

    public function delete(GroupInterface $group) : void;

    public function getGroupById(int $id) : GroupInterface;
    public function find(string $slug) : GroupInterface;

    public function all() : Collection;
}