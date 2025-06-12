<?php
namespace Iam\Groups\Controller\Api;

use Iam\Groups\Api\Data\StatusResponseInterface;
use Iam\Groups\Helper\UserGroupHelper;
use Iam\Groups\Model\StatusResponse;
use Iam\Groups\Service\UserGroupService;
use Iam\Groups\Helper\SystemEnvHelper;
use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\App\RequestInterface;


class AttachUserGroup
{
    protected UserGroupService $userGroupService;
    protected RequestInterface $request;

    public function __construct(
        UserGroupService $userGroupService,
        RequestInterface $request
    ) {
        $this->userGroupService = $userGroupService;
        $this->request = $request;
    }

    /**
     *
     * @return StatusResponseInterface
     */
    public function execute() : StatusResponseInterface
    {
        try {
            $token = SystemEnvHelper::get('IAM_GROUPS_ACCESS_TOKEN', '1234');

            if ($token !== $this->request->getHeader('X-Token-XPipe')) {
                LoggerFacade::error('Invalid Token');
                return new StatusResponse(403, false, 'Invalid Token');
            }

            $payload = json_decode($this->request->getContent(), true);

            if (UserGroupHelper::isPayloadValid($payload) === false) {
                return new StatusResponse(422, false, 'Invalid or malformed JSON payload');
            }

            $this->userGroupService->attachUserGroup($payload);

            $data = ['message' => 'User ' . $payload['username'] . ' successfully attached to group ' . $payload['group_slug']];

            return new StatusResponse(200, true, 'User attached successfully to group', $data);

        }
        catch (\Magento\Framework\Exception\NoSuchEntityException $e) {
            LoggerFacade::error('You are trying to attach a user to a group that does not exists', ['error' => $e]);
            return  new StatusResponse(404, false, 'You are trying to attach a user to a group that does not exists');
        }
        catch (\Magento\Framework\Exception\AlreadyExistsException $e) {
            LoggerFacade::error('User already attached to group', ['error' => $e]);
            return  new StatusResponse(409, false, 'User already attached to group');
        }
        catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return  new StatusResponse(500, false, 'Internal server error');
        }

    }
}
