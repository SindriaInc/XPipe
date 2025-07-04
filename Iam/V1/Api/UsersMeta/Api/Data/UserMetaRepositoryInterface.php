<?php

namespace Iam\UsersMeta\Api\Data;

use Iam\UsersMeta\Model\ResourceModel\UserMeta\Collection;

interface UserMetaRepositoryInterface
{
    public function save(array $payload) : UserMetaInterface;

    public function update(array $existingData, array $payload) : UserMetaInterface;

    public function delete(string $username) : UserMetaInterface;

    public function getUserMetaByUsername(string $username) : UserMetaInterface;
    public function find(string $username) : UserMetaInterface;

    public function all() : Collection;
}