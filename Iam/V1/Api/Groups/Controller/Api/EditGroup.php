<?php
namespace Iam\Groups\Controller\Api;

use Iam\Groups\Api\Data\StatusResponseInterface;
use Iam\Groups\Model\StatusResponse;
use Iam\Groups\Service\GroupService;
use Iam\Groups\Helper\SystemEnvHelper;
use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\App\RequestInterface;


class EditGroup
{
    protected GroupService $groupService;
    protected RequestInterface $request;

    public function __construct(
        GroupService     $groupService,
        RequestInterface $request
    ) {
        $this->groupService = $groupService;
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
