<?php
namespace Iam\Users\Service;

use Core\MicroFramework\Service\KeycloakService;
use Iam\Users\Helper\UserHelper;

class UserService extends KeycloakService
{


    public function __construct()
    {
        parent::__construct(
            UserHelper::getIamUsersIsBaseUrl(),
            UserHelper::getIamUsersIsRealm(),
            UserHelper::getIamUsersIsClientId(),
            UserHelper::getIamUsersIsClientSecret(),
            UserHelper::getIamUsersIsAdminRealm(),
            UserHelper::getIamUsersIsAdminClientId(),
            UserHelper::getIamUsersIsAdminClientSecret(),
            UserHelper::getIamUsersIsAdminUsername(),
            UserHelper::getIamUsersIsAdminPassword(),
        );
    }

//    public function getGroups($params)
//    {
//        $function = UserHelper::selectFunction($params);
//
//        switch ($function) {
//            case 0:
//                return 'test';
//            case 1:
//                return ['result' => 'ricerca'];
//            case 2:
//                return ['result' => 'paginazione'];
//            default:
//                return ['result' => 'parametri non supportati'];
//        }
//
//    }
//
//
//    public function listUsers()
//    {
//
//    }


}
