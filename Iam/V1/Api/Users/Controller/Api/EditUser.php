<?php
namespace Iam\Users\Controller\Api;



use Core\MicroFramework\Action\ValidateAccessTokenTrait;
use Iam\Groups\Helper\GroupHelper;
use Magento\Framework\App\RequestInterface;

use Core\MicroFramework\Api\Data\StatusResponseInterface;
use Core\MicroFramework\Model\StatusResponse;
use Core\Logger\Facade\LoggerFacade;

use Iam\Groups\Service\GroupService;

class EditUser
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
     *
     * @return StatusResponseInterface
     */
    public function execute() : StatusResponseInterface
    {

        try {
            $this->validateAccessToken($this->accessToken);

            $payload = json_decode($this->request->getContent(), true);

            if (!is_array($payload)) {
                return new StatusResponse(400, false, 'Invalid or malformed JSON payload');
            }

            $group = $this->groupService->editGroup($payload);

            $data = ['group' => $group];

            return new StatusResponse(200, true, 'ok', $data);

        }
        catch (\Iam\Groups\Exception\GroupSlugException $e) {
            LoggerFacade::error('Group slug cannot be changed', ['error' => $e]);
            return  new StatusResponse(422, false, 'Group slug cannot be changed');
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
