<?php
namespace Fnd\Notifications\Controller\Api;

use Fnd\Notifications\Helper\NotificationsHelper;
use Magento\Framework\App\RequestInterface;

use Core\MicroFramework\Api\Data\StatusResponseInterface;
use Core\MicroFramework\Model\StatusResponse;
use Core\Logger\Facade\LoggerFacade;
use Core\MicroFramework\Action\ValidateAccessTokenTrait;

use Core\Notifications\Service\NotificationService;


class Handle
{
    use ValidateAccessTokenTrait;

    protected NotificationService $notificationService;
    protected RequestInterface $request;
    private string $accessToken;

    public function __construct(
        NotificationService $notificationService,
        RequestInterface $request
    ) {
        $this->notificationService = $notificationService;
        $this->request = $request;
        $this->accessToken = NotificationsHelper::getFndNotificationsAccessToken();
    }

    /**
     * @return StatusResponseInterface
     */
    public function execute(): StatusResponseInterface
    {
        try {

            $this->validateAccessToken($this->accessToken);

            $payload = json_decode($this->request->getContent(), true);

            dd($this->request->getContent());

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
