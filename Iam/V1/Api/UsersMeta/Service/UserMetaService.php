<?php
namespace Iam\UsersMeta\Service;

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
    public function findUserMetaByUsername(string $username)
    {
        return $this->userMetaRepository->find($username)->getData();
    }


    /**
     * @throws AlreadyExistsException
     */
    public function createUserMeta(array $payload): array
    {
        return $this->userMetaRepository->save($payload)->getData();
    }


    /**
     * @throws NoSuchEntityException
     *
     */
    public function editUserMeta(array $payload): array
    {
        $userMetaData = $this->findUserMetaByUsername($payload['username']);

        return $this->userMetaRepository->update($userMetaData, $payload)->getData();
    }


    /**
     * @throws \Exception
     * @throws NoSuchEntityException
     */
    public function deleteUserMeta(string $username): array
    {
        $this->findUserMetaByUsername($username);
        return $this->userMetaRepository->delete($username)->getData();
    }


}
