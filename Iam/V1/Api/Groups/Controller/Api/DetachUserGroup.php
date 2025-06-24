<?php
namespace Iam\Groups\Controller\Api;


use Core\MicroFramework\Action\ValidateAccessTokenTrait;
use Iam\Groups\Helper\GroupHelper;
use Magento\Framework\App\RequestInterface;

use Core\MicroFramework\Api\Data\StatusResponseInterface;
use Core\MicroFramework\Model\StatusResponse;
use Core\Logger\Facade\LoggerFacade;

use Iam\Groups\Helper\UserGroupHelper;
use Iam\Groups\Service\UserGroupService;

class DetachUserGroup
{
    use ValidateAccessTokenTrait;
    protected UserGroupService $userGroupService;
    protected RequestInterface $request;
    private string $accessToken;

    public function __construct(
        UserGroupService $userGroupService,
        RequestInterface $request
    ) {
        $this->userGroupService = $userGroupService;
        $this->request = $request;
        $this->accessToken = GroupHelper::getIamGroupsAccessToken();
    }

    /**
     *
     * @return StatusResponseInterface
     */
    public function execute() : StatusResponseInterface
    {
        try {
            $this->validateAccessToken($this->accessToken);

            $payload = json_decode($this->request->getContent(), true);

            if (UserGroupHelper::isPayloadValid($payload) === false) {
                return new StatusResponse(422, false, 'Invalid or malformed JSON payload');
            }

            $this->userGroupService->detachUserGroup($payload);

            $data = ['message' => 'User ' . $payload['username'] . ' successfully detached from group ' . $payload['group_slug']];

            return new StatusResponse(200, true, 'User attached successfully to group', $data);

        }
        catch (\Magento\Framework\Exception\NoSuchEntityException $e) {
            LoggerFacade::error('You are trying to detach a user from a group that does not exists', ['error' => $e]);
            return  new StatusResponse(404, false, 'You are trying to detach a user from a group that does not exists');
        }
        catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return  new StatusResponse(500, false, 'Internal server error');
        }

    }
}
