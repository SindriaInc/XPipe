<?php
namespace Iam\UsersMeta\Controller\Api;

use Core\MicroFramework\Action\ValidateAccessTokenTrait;

use Iam\UsersMeta\Helper\UserMetaHelper;
use Iam\UsersMeta\Service\UserMetaService;
use Magento\Framework\App\RequestInterface;

use Core\MicroFramework\Api\Data\StatusResponseInterface;
use Core\MicroFramework\Model\StatusResponse;
use Core\Logger\Facade\LoggerFacade;


class DeleteUserMeta
{
    use ValidateAccessTokenTrait;

    protected UserMetaService $userMetaService;
    protected RequestInterface $request;
    private string $accessToken;

    public function __construct(
        UserMetaService  $userMetaService,
        RequestInterface $request
    ) {
        $this->userMetaService = $userMetaService;
        $this->request = $request;
        $this->accessToken = UserMetaHelper::getIamUserMetaAccessToken();
    }

    /**
     * @param string $username
     * @return StatusResponseInterface
     */
    public function execute(string $username) : StatusResponseInterface
    {

        try {
            $this->validateAccessToken($this->accessToken);

            $deletedUser = $this->userMetaService->deleteUserMeta($username);

            $data = $deletedUser;

            return new StatusResponse(200, true, 'ok', $data);

        }
        catch (\Magento\Framework\Exception\NoSuchEntityException $e) {
            LoggerFacade::error('User not found', ['error' => $e]);
            return  new StatusResponse(404, false, 'User not found');
        }
        catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return  new StatusResponse(500, false, 'Internal server error');
        }

    }
}
