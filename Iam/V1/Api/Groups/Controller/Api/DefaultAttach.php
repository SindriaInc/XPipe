<?php
namespace Iam\Groups\Controller\Api;


use Core\MicroFramework\Action\ValidateAccessTokenTrait;
use Iam\Groups\Helper\GroupHelper;
use Iam\Groups\Service\UserGroupService;
use Magento\Framework\App\RequestInterface;

use Core\MicroFramework\Api\Data\StatusResponseInterface;
use Core\MicroFramework\Model\StatusResponse;
use Core\Logger\Facade\LoggerFacade;



class DefaultAttach
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
     * @param string $username
     * @return StatusResponseInterface
     */
    public function execute(string $username) : StatusResponseInterface
    {
        try {
            $this->validateAccessToken($this->accessToken);

            $this->userGroupService->defaultAttachUserToGroups($username);

            $data = ['message' => 'User ' . $username . ' successfully attached to group xpipe-system'];

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
