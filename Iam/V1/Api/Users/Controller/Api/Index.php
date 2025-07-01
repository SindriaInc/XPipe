<?php
namespace Iam\Users\Controller\Api;

use Core\MicroFramework\Action\ValidateAccessTokenTrait;
use Iam\Users\Helper\UserHelper;
use Iam\Users\Service\UserService;
use Magento\Framework\App\RequestInterface;
use Core\MicroFramework\Api\Data\StatusResponseInterface;
use Core\MicroFramework\Model\StatusResponse;
use Core\Logger\Facade\LoggerFacade;


class Index
{
    use ValidateAccessTokenTrait;

    protected UserService $userService;
    protected RequestInterface $request;
    private string $accessToken;

    public function __construct(
        UserService      $userService,
        RequestInterface $request
    ) {
        $this->userService = $userService;
        $this->request = $request;
        $this->accessToken = UserHelper::getIamUsersAccessToken();
    }

    /**
     * @return StatusResponseInterface
     */
    public function execute() : StatusResponseInterface
    {
        try {
            $this->validateAccessToken($this->accessToken);

            //TODO get token from session
            $token = '';

            $users = $this->userService->listUsers($token);
            $data = ['users' => $users];

            return new StatusResponse(200, true, 'ok', $data);

        } catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return  new StatusResponse(500, false, 'Internal server error');
        }
    }
}
