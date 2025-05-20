<?php
namespace Core\Notifications\Controller\Api;

use Core\Notifications\Api\Data\StatusResponseInterface;
use Core\Notifications\Model\StatusResponse;
use Core\Notifications\Service\NotificationService;
use Core\Notifications\Helper\SystemEnvHelper;
use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\App\RequestInterface;

class Receive
{
    protected NotificationService $notificationService;
    protected RequestInterface $request;

    public function __construct(
        NotificationService $notificationService,
        RequestInterface $request
    ) {
        $this->notificationService = $notificationService;
        $this->request = $request;
    }

    /**
     * @return StatusResponseInterface
     */
    public function execute(): StatusResponseInterface
    {
        try {
            $token = SystemEnvHelper::get('NOTIFICATIONS_TOKEN', '1234');
            $payload = json_decode($this->request->getContent(), true);

            if (!is_array($payload)) {
                return new StatusResponse(400, false, 'Invalid or malformed JSON payload');
            }

            if ($token !== $this->request->getParam('token')) {
                LoggerFacade::error('Invalid Token');
                return new StatusResponse(403, false, 'Invalid Token');
            }

            $this->notificationService->addNotification($payload);

            return new StatusResponse(200, true, 'Notification received');

        } catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return new StatusResponse(500, false, 'Internal server error');
        }
    }
}
