<?php
namespace Iam\Users\Controller\Api;

use Core\MicroFramework\Action\ValidateAccessTokenTrait;
use Iam\Users\Helper\UserHelper;
use Iam\Users\Service\UserService;
use Magento\Framework\App\RequestInterface;
use Core\MicroFramework\Api\Data\StatusResponseInterface;
use Core\MicroFramework\Model\StatusResponse;
use Core\Logger\Facade\LoggerFacade;


class SearchUsers
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
            $query = $this->request->getParam('q');

            $this->validateAccessToken($this->accessToken);

            $result = $this->userService->searchUsers($query);

            $data = ['users' => $result];

            return new StatusResponse(200, true, 'ok', $data);
        }
        catch (\Magento\Framework\Exception\NotFoundException $e) {
            LoggerFacade::error('User not found', ['error' => $e]);
            return  new StatusResponse(404, false, 'User not found');
        }
        catch (\Symfony\Component\HttpKernel\Exception\UnauthorizedHttpException $e) {
            LoggerFacade::error('Unauthorized', ['error' => $e]);
            return  new StatusResponse(401, false, 'Unauthorized');
        }
        catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return  new StatusResponse(500, false, 'Internal server error');
        }
    }
}
