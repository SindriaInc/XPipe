<?php
namespace Iam\Groups\Controller\Api;

use Core\MicroFramework\Action\ValidateAccessTokenTrait;
use Iam\Groups\Helper\GroupHelper;
use Magento\Framework\App\RequestInterface;

use Core\MicroFramework\Api\Data\StatusResponseInterface;
use Core\MicroFramework\Model\StatusResponse;
use Core\Logger\Facade\LoggerFacade;

use Iam\Groups\Service\GroupService;


class DeleteGroup
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
        $this->accessToken = GroupHelper::getIamGroupsAccessToken();
    }

    /**
     * @param string $slug
     * @return StatusResponseInterface
     */
    public function execute(string $slug) : StatusResponseInterface
    {

        try {
            $this->validateAccessToken($this->accessToken);

            $deleteGroup = $this->groupService->deleteGroup($slug);

            $data = ['group' => $deleteGroup];

            return new StatusResponse(200, true, 'ok', $data);

        }
        catch (\Magento\Framework\Exception\NoSuchEntityException $e) {
            LoggerFacade::error('Group not found', ['error' => $e]);
            return  new StatusResponse(404, false, 'Group not found');
        }
        catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return  new StatusResponse(500, false, 'Internal server error');
        }

    }
}
