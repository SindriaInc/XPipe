<?php
namespace Iam\Users\Controller\Api;

use Core\MicroFramework\Action\ValidateAccessTokenTrait;
use Iam\Users\Helper\UserHelper;
use Magento\Framework\App\RequestInterface;
use Core\MicroFramework\Api\Data\StatusResponseInterface;
use Core\MicroFramework\Model\StatusResponse;
use Core\Logger\Facade\LoggerFacade;

use Iam\Groups\Service\GroupService;

class UserSession
{
    use ValidateAccessTokenTrait;

    protected GroupService $groupService;
    protected RequestInterface $request;
    private string $accessToken;

    public function __construct(
        GroupService     $groupService,
        RequestInterface $request
    ) {
        $this->groupService = $groupService;
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

            $params = $this->request->getParams();

            $groups = $this->groupService->getGroups($params);

            $data = ['groups' => $groups];

            return new StatusResponse(200, true, 'ok', $data);

        } catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return  new StatusResponse(500, false, 'Internal server error');
        }
    }
}
