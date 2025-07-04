<?php
namespace Iam\Groups\Service;

use Iam\Groups\Repository\GroupRepository;
use Iam\Groups\Repository\UserGroupRepository;
use Magento\Framework\Exception\AlreadyExistsException;
use Magento\Framework\Exception\NoSuchEntityException;

class UserGroupService
{

    private UserGroupRepository $userGroupRepository;
    private GroupRepository $groupRepository;

    public function __construct(UserGroupRepository $userGroupRepository, GroupRepository $groupRepository)
    {
        $this->userGroupRepository = $userGroupRepository;
        $this->groupRepository = $groupRepository;
    }

    /**
     * @throws NoSuchEntityException
     * @throws AlreadyExistsException
     */
    public function attachUserGroup(array $payload) : void
    {
        $username = $payload['username'];
        $groupSlug = $payload['group_slug'];
        $group = $this->groupRepository->find($groupSlug);
        $this->userGroupRepository->attach($username, $group->getGroupId());

    }

    /**
     * @throws NoSuchEntityException
     * @throws \Exception
     */
    public function detachUserGroup(array $payload) : void
    {
        $username = $payload['username'];
        $groupSlug = $payload['group_slug'];
        $group = $this->groupRepository->find($groupSlug);
        $this->userGroupRepository->detach($username, $group->getGroupId());

    }

    /**
     * @throws \Zend_Db_Statement_Exception
     */
    public function findAttachedUsers(string $groupSlug) : array
    {
        return $this->userGroupRepository->attachedUsers($groupSlug);
    }

    /**
     * @throws \Zend_Db_Statement_Exception
     */
    public function findAttachedGroups(string $username) : array
    {
        return $this->userGroupRepository->attachedGroups($username);
    }

    /**
     * @throws NoSuchEntityException
     * @throws AlreadyExistsException
     */
    public function defaultAttachUserToGroups(string $username) : void
    {
        //TODO: aggiungere campo system-defined al gruppo, per creare una query che attaccha dinamicamente tutti i gruppi di sistema.
        $groupSlug = 'xpipe-system';
        $group = $this->groupRepository->find($groupSlug);
        $this->userGroupRepository->attach($username, $group->getGroupId());

    }


}
