<?php

namespace Iam\Groups\Repository;

use Iam\Groups\Api\Data\UserGroupInterface;
use Iam\Groups\Api\Data\UserGroupRepositoryInterface;
use Iam\Groups\Model\UserGroupFactory;
use Iam\Groups\Model\ResourceModel\UserGroup as UserGroupResource;
use Iam\Groups\Model\ResourceModel\UserGroup\Collection;
use Iam\Groups\Model\ResourceModel\UserGroup\CollectionFactory;
use Magento\Framework\Exception\AlreadyExistsException;
use Magento\Framework\Exception\NoSuchEntityException;

class UserGroupRepository implements UserGroupRepositoryInterface
{

    private UserGroupResource $resource;

    private UserGroupFactory $factory;

    private CollectionFactory $collectionFactory;

    public function __construct(UserGroupResource $resource, UserGroupFactory $factory, CollectionFactory $collectionFactory)
    {
        $this->resource = $resource;
        $this->factory = $factory;
        $this->collectionFactory = $collectionFactory;
    }

    /**
     * @throws AlreadyExistsException
     */
    public function attach(string $username, int $groupId): void
    {
        $model = $this->factory->create();
        $model->setData('username', $username);
        $model->setData('group_id', $groupId);
        $this->resource->save($model);

    }

    public function update(array $existingData, array $payload): UserGroupInterface
    {
        // TODO: Implement update() method.
    }

    public function delete(string $slug): UserGroupInterface
    {
        // TODO: Implement delete() method.
    }

    public function getUserGroupById(int $id): UserGroupInterface
    {
        // TODO: Implement getUserGroupById() method.
    }

    public function find(string $slug): UserGroupInterface
    {
        // TODO: Implement find() method.
    }

    public function all(): Collection
    {
        // TODO: Implement all() method.
    }


//    /**
//     * @throws AlreadyExistsException
//     */
//    public function save(array $payload): GroupInterface
//    {
//        $model = $this->factory->create();
//        $model->setData($payload);
//        $this->resource->save($model);
//        return $model;
//    }
//    public function update(array $existingData, array $payload): GroupInterface
//    {
//        $model = $this->factory->create();
//        $this->resource->load($model, $existingData['slug'], 'slug');
//
//        $model->setData('label',$payload['label']);
//        $model->setData('short',$payload['short']);
//
//        $this->resource->save($model);
//        return $model;
//
//    }
//
//
//    /**
//     * @throws \Exception
//     */
//    public function delete(string $slug): GroupInterface
//    {
//        $model = $this->factory->create();
//        $this->resource->load($model, $slug, 'slug');
//
//        $this->resource->delete($model);
//        return $model;
//    }
//
//    public function getGroupById(int $id): GroupInterface
//    {
//        $group = $this->factory->create();
//        $this->resource->load($group, $id);
//        if (!$group->getGroupId()) {
//            throw new NoSuchEntityException(__('Group with id "%1" does not exist.', $id));
//        }
//        return $group;
//    }
//
//    public function find(string $slug): GroupInterface
//    {
//        $group = $this->factory->create();
//        $this->resource->load($group, $slug, 'slug');
//
//        if (!$group->getSlug()) {
//            throw new NoSuchEntityException(__('Group with slug "%1" does not exist.', $slug));
//        }
//        return $group;
//    }
//
//    public function all(): Collection
//    {
//        return $this->collectionFactory->create();
//    }



}