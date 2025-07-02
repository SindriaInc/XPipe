<?php
namespace Iam\Users\Service;

use Core\Http\Facade\HttpFacade;
use Core\MicroFramework\Service\KeycloakService;
use Iam\Users\Helper\UserHelper;
use Magento\Framework\Exception\AlreadyExistsException;

class UserService extends KeycloakService
{

    private string $serviceAccountUsername;
    private string $serviceAccountPassword;
    private array $oauthLogin;
    private string $accessToken;

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

        $this->serviceAccountUsername = UserHelper::getIamUsersIsServiceAccountUsername();
        $this->serviceAccountPassword = UserHelper::getIamUsersIsServiceAccountPassword();
        $this->oauthLogin = $this->login($this->serviceAccountUsername, $this->serviceAccountPassword)['data'];
        $this->accessToken = $this->oauthLogin['access_token'];
    }

    /**
     * @throws AlreadyExistsException
     * @throws \Exception
     */
    public function createUser(array $payload): array
    {
        $result = $this->keycloakCreateUser($payload, $this->accessToken);

        if ($result['code'] === 201) {
            $this->logout($this->accessToken);
            return $result['data']['user'];
        }

        if ($result['code'] === 409) {
            $this->logout($this->accessToken);
            throw new \Magento\Framework\Exception\AlreadyExistsException();
        }

        $this->logout($this->accessToken);
        throw new \Exception([]);
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
