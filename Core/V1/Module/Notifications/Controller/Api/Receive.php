<?php
namespace Core\Notifications\Controller\Api;

use Core\Notifications\Service\NotificationService;
use Core\Notifications\Helper\SystemEnvHelper;
use Core\Notifications\Helper\ApiResponseHelper;
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
     * @return array
     */
    public function execute(): array
    {
        try {
            $token = SystemEnvHelper::get('NOTIFICATIONS_TOKEN', '1234');
            $payload = json_decode($this->request->getContent(), true);

            if (!is_array($payload)) {
                return ApiResponseHelper::sendError(400, 'Invalid or malformed JSON payload');
            }

            if ($token !== $this->request->getParam('token')) {
                LoggerFacade::error('Invalid Token');
                return ApiResponseHelper::sendError(403, 'Invalid Token');
            }

            $this->notificationService->addNotification($payload);

            return ApiResponseHelper::sendSuccess(200, 'Notification received');

        } catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return ApiResponseHelper::sendError(500, 'Internal server error');
        }
    }
}
