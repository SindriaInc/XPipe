<?php

namespace Iam\Groups\Repository;

use Iam\Groups\Api\Data\UserGroupRepositoryInterface;
use Iam\Groups\Model\UserGroupFactory;
use Iam\Groups\Model\ResourceModel\UserGroup as UserGroupResource;
use Iam\Groups\Model\ResourceModel\UserGroup\CollectionFactory;
use Magento\Framework\Exception\AlreadyExistsException;
use Magento\Framework\Exception\NoSuchEntityException;
use Core\QueryBuilder\Facade\QueryFacade;

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

    /**
     * @throws \Exception
     */
    public function detach(string $username, int $groupId): void
    {
        $model = $this->getUserGroupByUsernameAndGroupId($username, $groupId);

        $this->resource->delete($model);


    }

    public function getUserGroupByUsernameAndGroupId(string $username, int $groupId)
    {
        $collection = $this->collectionFactory->create();

        $collection->addFieldToFilter('username', $username);
        $collection->addFieldToFilter('group_id', $groupId);
        return $collection->getFirstItem();
    }


    /**
     * @throws \Zend_Db_Statement_Exception
     */
    public function attachedUsers(string $groupSlug): array
    {
        $table = 'iam_user_group';

        // Native query
        //$sql = "SELECT iug.username FROM " . $table . " iug JOIN iam_groups ig ON iug.group_id = ig.group_id WHERE ig.slug = 'circolo-biliardo'";

        $sql = "SELECT iug.username 
            FROM {$table} iug 
            JOIN iam_groups ig ON iug.group_id = ig.group_id 
            WHERE ig.slug = :slug";

        $statement = QueryFacade::query($table, $sql, ['slug' => $groupSlug]);

        return $statement->fetchAll();
    }
}