<?php
namespace Iam\UsersMeta\Service;

use Iam\Groups\Helper\GroupHelper;
use Iam\UsersMeta\Repository\UserMetaRepository;
use Magento\Framework\Exception\AlreadyExistsException;
use Magento\Framework\Exception\NoSuchEntityException;

class UserMetaService
{

    private UserMetaRepository $userMetaRepository;

    public function __construct(UserMetaRepository $userMetaRepository)
    {
        $this->userMetaRepository = $userMetaRepository;
    }


    public function listUserMeta()
    {
        return $this->userMetaRepository->all()->getData();
    }

//    public function getGroups($params)
//    {
//        $function = GroupHelper::selectFunction($params);
//
//        switch ($function) {
//            case 0:
//                return $this->userMetaRepository->all()->getData();
//            case 1:
//                return ['result' => 'ricerca'];
//            case 2:
//                return ['result' => 'paginazione'];
//            default:
//                return ['result' => 'parametri non supportati'];
//        }
//
//
//    }


    /**
     * @throws NoSuchEntityException
     */
    public function findGroupBySlug(string $slug)
    {
        return $this->userMetaRepository->find($slug)->getData();
    }


    /**
     * @throws AlreadyExistsException
     */
    public function createGroup(array $payload): array
    {
        return $this->userMetaRepository->save($payload)->getData();
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

        return $this->userMetaRepository->update($groupData, $payload)->getData();
    }


    /**
     * @throws \Exception
     * @throws NoSuchEntityException
     */
    public function deleteGroup(string $slug): array
    {
        $this->findGroupBySlug($slug);
        return $this->userMetaRepository->delete($slug)->getData();
    }


}
