<?php
namespace Iam\Groups\Service;

use Iam\Groups\Helper\GroupHelper;
use Iam\Groups\Repository\GroupRepository;
use Magento\Framework\Exception\AlreadyExistsException;
use Magento\Framework\Exception\NoSuchEntityException;

class GroupsService
{

    private GroupRepository $groupRepository;

    public function __construct(GroupRepository $groupRepository)
    {
        $this->groupRepository = $groupRepository;
    }

    public function getGroups($params)
    {
        $function = GroupHelper::choosedFunction($params);

        switch ($function) {
            case 0:
                return $this->groupRepository->all()->getData();
                break;
            case 1:
                return ['result' => 'ricerca'];
                break;
            case 2:
                return ['result' => 'paginazione'];
                break;
            default:
                return ['result' => 'parametri non supportati'];
                break;
        }


    }

    /**
     * @throws NoSuchEntityException
     */
    public function findGroupBySlug(string $slug)
    {
        return $this->groupRepository->find($slug)->getData();
    }

    /**
     * @throws AlreadyExistsException
     */
    public function createGroup(array $payload): array
    {
        return $this->groupRepository->save($payload)->getData();
    }


    /**
     * @throws \Iam\Groups\Exception\GroupSlugException|NoSuchEntityException
     *
     */
    public function editGroup(array $payload): array
    {
        $groupData = $this->findGroupBySlug($payload['slug']);

        // Non serve ma e' bella
        if ($payload['slug'] !== $groupData['slug']) {
            throw new \Iam\Groups\Exception\GroupSlugException('Group slug cannot be changed');
        }

        return $this->groupRepository->update($groupData, $payload)->getData();
    }


    /**
     * @throws \Exception
     * @throws NoSuchEntityException
     */
    public function deleteGroup(string $slug): array
    {
        $this->findGroupBySlug($slug);
        return $this->groupRepository->delete($slug)->getData();
    }


}
