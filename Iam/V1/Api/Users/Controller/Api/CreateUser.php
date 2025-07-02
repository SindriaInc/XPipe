<?php
namespace Iam\Users\Controller\Api;

use Core\MicroFramework\Action\ValidateAccessTokenTrait;
use Iam\Users\Helper\UserHelper;
use Iam\Users\Service\UserService;
use Magento\Framework\App\RequestInterface;

use Core\MicroFramework\Api\Data\StatusResponseInterface;
use Core\MicroFramework\Model\StatusResponse;
use Core\Logger\Facade\LoggerFacade;


class CreateUser
{
    use ValidateAccessTokenTrait;

    protected UserService $userService;
    protected RequestInterface $request;
    private string $accessToken;

    public function __construct(
        UserService      $userService,
        RequestInterface $request
    ) {
        $this->userService = $userService;
        $this->request = $request;
        $this->accessToken = UserHelper::getIamUsersAccessToken();
    }

    /**
     *
     * @return StatusResponseInterface
     */
    public function execute() : StatusResponseInterface
    {

        try {
            $this->validateAccessToken($this->accessToken);

            $isJsonValid = UserHelper::isJson($this->request->getContent());

            if ($isJsonValid === false) {
                return new StatusResponse(400, false, 'Syntax Error: Invalid or malformed JSON payload');
            }

            $payload = json_decode($this->request->getContent(), true);

            $isPayloadValid = UserHelper::validatePayload($payload);

            if ($isPayloadValid === false) {
                LoggerFacade::error('Iam_Users::CreateUser - Semantic Error: Invalid or malformed JSON payload');
                return new StatusResponse(422, false, 'Semantic Error: Invalid or malformed JSON payload');
            }

            $user = $this->userService->createUser($payload);

            $data = ['user' => $user];

            return new StatusResponse(200, true, 'ok', $data);

        } catch (\Magento\Framework\Exception\AlreadyExistsException $e) {
            LoggerFacade::error('User already exists', ['error' => $e]);
            return  new StatusResponse(409, false, 'User already exists');
        }
        catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return  new StatusResponse(500, false, 'Internal server error');
        }

    }
}
