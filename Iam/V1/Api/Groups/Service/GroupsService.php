<?php
namespace Iam\Groups\Service;

use Iam\Groups\Repository\GroupRepository;

class GroupsService
{

    private GroupRepository $groupRepository;

    public function __construct(GroupRepository $groupRepository)
    {
        $this->groupRepository = $groupRepository;
    }

    public function getAllGroups()
    {
        return $this->groupRepository->all()->getData();
    }


}
