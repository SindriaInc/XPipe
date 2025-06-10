<?php

namespace Iam\Groups\Api;

use Iam\Groups\Model\ResourceModel\Group\Collection;
use Iam\Groups\Api\Data\GroupInterface;

interface GroupRepositoryInterface
{
    public function save(GroupInterface $group) : void;

    public function delete(GroupInterface $group) : void;

    public function getGroupById(int $id) : GroupInterface;

    public function all() : Collection;
}