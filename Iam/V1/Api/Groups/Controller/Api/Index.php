<?php
namespace Iam\Groups\Controller\Api;

use Iam\Groups\Api\Data\StatusResponseInterface;
use Iam\Groups\Model\StatusResponse;
use Iam\Groups\Service\GroupsService;
use Iam\Groups\Helper\SystemEnvHelper;
use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\App\RequestInterface;

class Index
{
    protected GroupsService $groupsService;
    protected RequestInterface $request;

    public function __construct(
        GroupsService    $groupsService,
        RequestInterface $request
    ) {
        $this->groupsService = $groupsService;
        $this->request = $request;
    }

    /**
     * @return StatusResponseInterface
     */
    public function execute(): StatusResponseInterface
    {

        dd($this->request);
//        try {
//            $token = SystemEnvHelper::get('NOTIFICATIONS_TOKEN', '1234');
//            $payload = json_decode($this->request->getContent(), true);
//
//            if (!is_array($payload)) {
//                return new StatusResponse(400, false, 'Invalid or malformed JSON payload');
//            }
//
//            if ($token !== $this->request->getParam('token')) {
//                LoggerFacade::error('Invalid Token');
//                return new StatusResponse(403, false, 'Invalid Token');
//            }
//
//            $this->groupsService->addNotification($payload);
//
//            return new StatusResponse(200, true, 'Notification received');
//
//        } catch (\Exception $e) {
//            LoggerFacade::error('Internal error', ['error' => $e]);
//            return new StatusResponse(500, false, 'Internal server error');
//        }
    }
}
