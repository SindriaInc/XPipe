<?php
namespace Iam\Groups\Controller\Api;


use Core\MicroFramework\Action\ValidateAccessTokenTrait;
use Iam\Groups\Helper\GroupHelper;
use Magento\Framework\App\RequestInterface;

use Core\MicroFramework\Api\Data\StatusResponseInterface;
use Core\MicroFramework\Model\StatusResponse;
use Core\Logger\Facade\LoggerFacade;

use Iam\Groups\Service\UserGroupService;


class AttachedUsers
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
     * @param string $group_slug
     * @return StatusResponseInterface
     */
    public function execute(string $group_slug) : StatusResponseInterface
    {

        try {
            $this->validateAccessToken($this->accessToken);

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
