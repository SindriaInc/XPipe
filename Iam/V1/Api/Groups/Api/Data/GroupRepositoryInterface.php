<?php

namespace Iam\Groups\Api\Data;

use Iam\Groups\Model\ResourceModel\Group\Collection;

interface GroupRepositoryInterface
{
    public function save(GroupInterface $group) : void;

    public function delete(GroupInterface $group) : void;

    public function getGroupById(int $id) : GroupInterface;

    public function all() : Collection;
}