<?php
namespace Iam\Groups\Controller\Api;

use Iam\Groups\Api\Data\StatusResponseInterface;
use Iam\Groups\Model\StatusResponse;
use Iam\Groups\Service\UserGroupService;
use Iam\Groups\Helper\SystemEnvHelper;
use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\App\RequestInterface;


class AttachedGroups
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
     * @param string $username
     * @return StatusResponseInterface
     */
    public function execute(string $username) : StatusResponseInterface
    {
        try {
            $token = SystemEnvHelper::get('IAM_GROUPS_ACCESS_TOKEN', '1234');

            if ($token !== $this->request->getHeader('X-Token-XPipe')) {
                LoggerFacade::error('Invalid Token');
                return new StatusResponse(403, false, 'Invalid Token');
            }

            $attachedGroups = $this->userGroupService->findAttachedGroups($username);

            $data = ['groups' => $attachedGroups];

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
