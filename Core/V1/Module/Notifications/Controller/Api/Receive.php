<?php
namespace Core\Notifications\Controller\Api;

use Magento\Framework\App\RequestInterface;

use Core\MicroFramework\Api\Data\StatusResponseInterface;
use Core\MicroFramework\Model\StatusResponse;
use Core\Logger\Facade\LoggerFacade;

use Core\Notifications\Service\NotificationService;
use Core\Notifications\Helper\NotificationsHelper;

class Receive
{
    protected NotificationService $notificationService;
    protected RequestInterface $request;

    private string $accessToken;

    public function __construct(
        NotificationService $notificationService,
        RequestInterface $request
    ) {
        $this->notificationService = $notificationService;
        $this->request = $request;
        $this->accessToken = NotificationsHelper::getCoreNotificationsAccessToken();
    }

    /**
     * Validate Access Token with request X-Token-XPipe header
     *
     * @param string $accessToken
     * @return StatusResponseInterface|void
     */
    private function validateAccessToken(string $accessToken)
    {
        if ($accessToken !== $this->request->getHeader('X-Token-XPipe')) {
            LoggerFacade::error('Invalid Token');
            return new StatusResponse(403, false, 'Invalid Token');
        }
    }

    /**
     * @return StatusResponseInterface
     */
    public function execute(): StatusResponseInterface
    {
        try {
            $this->validateAccessToken($this->accessToken);

            $payload = json_decode($this->request->getContent(), true);

            if (!is_array($payload)) {
                return new StatusResponse(400, false, 'Invalid or malformed JSON payload');
            }

            $this->notificationService->addNotification($payload);

            return new StatusResponse(200, true, 'Notification received');

        } catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return new StatusResponse(500, false, 'Internal server error');
        }
    }
}
