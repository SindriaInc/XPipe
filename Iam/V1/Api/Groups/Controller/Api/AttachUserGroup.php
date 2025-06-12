<?php
namespace Iam\Groups\Controller\Api;

use Iam\Groups\Api\Data\StatusResponseInterface;
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
        dd('attach user group');

        try {
            $token = SystemEnvHelper::get('IAM_GROUPS_ACCESS_TOKEN', '1234');

            if ($token !== $this->request->getHeader('X-Token-XPipe')) {
                LoggerFacade::error('Invalid Token');
                return new StatusResponse(403, false, 'Invalid Token');
            }

            $payload = json_decode($this->request->getContent(), true);

            if (!is_array($payload)) {
                return new StatusResponse(400, false, 'Invalid or malformed JSON payload');
            }

            $group = $this->userGroupService->createGroup($payload);

            $data = ['group' => $group];

            return new StatusResponse(200, true, 'ok', $data);

        } catch (\Magento\Framework\Exception\AlreadyExistsException $e) {
            LoggerFacade::error('Group already exists', ['error' => $e]);
            return  new StatusResponse(409, false, 'Group already exists');
        }
        catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return  new StatusResponse(500, false, 'Internal server error');
        }

    }
}
