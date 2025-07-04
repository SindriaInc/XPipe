<?php
namespace Iam\Users\Service;

use Core\MicroFramework\Service\KeycloakService;
use Iam\Users\Helper\UserHelper;
use Magento\Framework\Exception\AlreadyExistsException;
use Magento\Framework\Exception\NotFoundException;
use Magento\Framework\Phrase;
use Symfony\Component\HttpKernel\Exception\UnauthorizedHttpException;


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
        $this->oauthLogin = $this->keycloakLogin($this->serviceAccountUsername, $this->serviceAccountPassword)['data'];
        $this->accessToken = $this->oauthLogin['access_token'];
    }


    /**
     * @throws NotFoundException
     * @throws UnauthorizedHttpException
     * @throws \Exception
     */
    public function listUsers(): array
    {
        $result = $this->keycloakListUsers($this->accessToken);

        if ($result['code'] === 200) {
            $this->keycloakLogout($this->accessToken);
            return $result['data']['users'];
        }

        if ($result['code'] === 404) {
            $this->keycloakLogout($this->accessToken);
            throw new NotFoundException(new Phrase('User not found'));
        }

        if ($result['code'] === 401) {
            $this->keycloakLogout($this->accessToken);
            throw new UnauthorizedHttpException();
        }

        $this->keycloakLogout($this->accessToken);
        throw new \Exception([]);

    }


    /**
     * @throws NotFoundException
     * @throws UnauthorizedHttpException
     * @throws \Exception
     */
    public function getUserByUuid(string $uuid): array
    {

        $result = $this->keycloakGetUserByUuid($uuid, $this->accessToken);

        if ($result['code'] === 200) {
            $this->keycloakLogout($this->accessToken);
            return $result['data']['user'];
        }

        if ($result['code'] === 404) {
            $this->keycloakLogout($this->accessToken);
            throw new NotFoundException(new Phrase('User not found'));
        }

        if ($result['code'] === 401) {
            $this->keycloakLogout($this->accessToken);
            throw new UnauthorizedHttpException();
        }

        $this->keycloakLogout($this->accessToken);
        throw new \Exception([]);
    }

    /**
     * @throws NotFoundException
     * @throws UnauthorizedHttpException
     * @throws \Exception
     */
    public function getUserByUsername(string $username): array
    {

        $result = $this->keycloakGetUserByUsername($username, $this->accessToken);

        if ($result['code'] === 200) {
            $this->keycloakLogout($this->accessToken);
            return $result['data'];
        }

        if ($result['code'] === 404) {
            $this->keycloakLogout($this->accessToken);
            throw new NotFoundException(new Phrase('User not found'));
        }

        if ($result['code'] === 401) {
            $this->keycloakLogout($this->accessToken);
            throw new UnauthorizedHttpException();
        }

        $this->keycloakLogout($this->accessToken);
        throw new \Exception([]);
    }


    /**
     * @throws AlreadyExistsException
     * @throws UnauthorizedHttpException
     * @throws \Exception
     */
    public function createUser(array $payload): array
    {
        $result = $this->keycloakCreateUser($payload, $this->accessToken);

        if ($result['code'] === 201) {
            $this->keycloakLogout($this->accessToken);
            return $result['data']['user'];
        }

        if ($result['code'] === 401) {
            $this->keycloakLogout($this->accessToken);
            throw new UnauthorizedHttpException();
        }

        if ($result['code'] === 409) {
            $this->keycloakLogout($this->accessToken);
            throw new \Magento\Framework\Exception\AlreadyExistsException();
        }

        $this->keycloakLogout($this->accessToken);
        throw new \Exception([]);
    }


    /**
     * @throws NotFoundException
     * @throws UnauthorizedHttpException
     * @throws \Exception
     */
    public function editUser(string $uuid, array $payload): array
    {
        $result = $this->keycloakEditUser($uuid, $payload, $this->accessToken);

        if ($result['code'] === 204) {
            $this->keycloakLogout($this->accessToken);
            return $payload;
        }

        if ($result['code'] === 401) {
            $this->keycloakLogout($this->accessToken);
            throw new UnauthorizedHttpException();
        }

        if ($result['code'] === 404) {
            $this->keycloakLogout($this->accessToken);
            throw new NotFoundException(new Phrase('User not found'));
        }

        $this->keycloakLogout($this->accessToken);
        throw new \Exception([]);
    }

    /**
     * @throws NotFoundException
     * @throws UnauthorizedHttpException
     * @throws \Exception
     */
    public function deleteUser(string $uuid): array
    {
        $result = $this->keycloakDeleteUser($uuid, $this->accessToken);

        if ($result['code'] === 204) {
            $this->keycloakLogout($this->accessToken);
            return $result['data'];
        }

        if ($result['code'] === 401) {
            $this->keycloakLogout($this->accessToken);
            throw new UnauthorizedHttpException();
        }

        if ($result['code'] === 404) {
            $this->keycloakLogout($this->accessToken);
            throw new NotFoundException(new Phrase('User not found'));
        }

        $this->keycloakLogout($this->accessToken);
        throw new \Exception([]);
    }


    /**
     * @throws NotFoundException
     * @throws UnauthorizedHttpException
     * @throws \Exception
     */
    public function loggedUser(): array
    {
        $result = $this->keycloakLoggedUser($this->accessToken);

        if ($result['code'] === 200) {
            $this->keycloakLogout($this->accessToken);
            return $result['data']['users'];
        }

        if ($result['code'] === 404) {
            $this->keycloakLogout($this->accessToken);
            throw new NotFoundException();
        }

        if ($result['code'] === 401) {
            $this->keycloakLogout($this->accessToken);
            throw new UnauthorizedHttpException();
        }

        $this->keycloakLogout($this->accessToken);
        throw new \Exception([]);

    }


    /**
     * @throws NotFoundException
     * @throws UnauthorizedHttpException
     * @throws \Exception
     */
    public function searchUsers(string $query): array
    {
        $result = $this->keycloakSearchUsers($query, $this->accessToken);

        if ($result['code'] === 200) {
            $this->keycloakLogout($this->accessToken);
            return $result['data']['users'];
        }

        if ($result['code'] === 404) {
            $this->keycloakLogout($this->accessToken);
            throw new NotFoundException(new Phrase('User not found'));
        }

        if ($result['code'] === 401) {
            $this->keycloakLogout($this->accessToken);
            throw new UnauthorizedHttpException();
        }

        $this->keycloakLogout($this->accessToken);
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
