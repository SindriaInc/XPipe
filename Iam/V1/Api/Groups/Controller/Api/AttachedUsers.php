<?php
namespace Iam\Groups\Controller\Api;

use Iam\Groups\Api\Data\StatusResponseInterface;
use Iam\Groups\Model\StatusResponse;
use Iam\Groups\Service\UserGroupService;
use Iam\Groups\Helper\SystemEnvHelper;
use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\App\RequestInterface;


class AttachedUsers
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
     * @param string $group_slug
     * @return StatusResponseInterface
     */
    public function execute(string $group_slug) : StatusResponseInterface
    {

        try {
            $token = SystemEnvHelper::get('IAM_GROUPS_ACCESS_TOKEN', '1234');

            if ($token !== $this->request->getHeader('X-Token-XPipe')) {
                LoggerFacade::error('Invalid Token');
                return new StatusResponse(403, false, 'Invalid Token');
            }

            $attachedUsers = $this->userGroupService->findAttachedUsers($group_slug);

            $data = ['users' => $attachedUsers];

            return new StatusResponse(200, true, 'ok', $data);

        } catch (\Zend_Db_Statement_Exception $e) {
            LoggerFacade::error('Error during database statement execution', ['error' => $e]);
            return  new StatusResponse(500, false, 'Error during database statement execution');
        }
        catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return  new StatusResponse(500, false, 'Internal server error');
        }

    }
}
