<?php
namespace Iam\Users\Controller\Api;

use Core\MicroFramework\Action\ValidateAccessTokenTrait;
use Iam\Users\Helper\UserHelper;
use Iam\Users\Service\UserService;
use Magento\Framework\App\RequestInterface;
use Core\MicroFramework\Api\Data\StatusResponseInterface;
use Core\MicroFramework\Model\StatusResponse;
use Core\Logger\Facade\LoggerFacade;


class Index
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
     * @return StatusResponseInterface
     */
    public function execute() : StatusResponseInterface
    {
        try {
            $this->validateAccessToken($this->accessToken);

            $params = $this->request->getParams();

            $test = $this->userService->logout('eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJiYllhNDd6NzFjNTUwMGpTREZBZ1cxUnl6Q0ZnN0dmdF9sRzZIODMyNmtjIn0.eyJleHAiOjE3NTEzNzE5NTQsImlhdCI6MTc1MTM3MTY1MywianRpIjoiN2I5NGIyNWEtYjM2ZS00MDJmLWJjMzAtMjk3ZDk2YTkwYjdmIiwiaXNzIjoiaHR0cHM6Ly9kZXYtYXV0aC14cGlwZS5zaW5kcmlhLm9yZy9hdXRoL3JlYWxtcy9zaW5kcmlhIiwiYXVkIjpbInJlYWxtLW1hbmFnZW1lbnQiLCJhY2NvdW50Il0sInN1YiI6ImIyMDMzY2VhLTJlYTYtNGFjZC05Yzc1LTJhMmZkYzYzOGJkOSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImRldi14cGlwZS1jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiNjI5MTVmOTctMDFiNC00YjY0LTg4NzYtODMwM2Q5MWRjOWNlIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL2Rldi1kYXNoYm9hcmQteHBpcGUuc2luZHJpYS5vcmciXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtc2luZHJpYSIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJyZWFsbS1tYW5hZ2VtZW50Ijp7InJvbGVzIjpbInZpZXctaWRlbnRpdHktcHJvdmlkZXJzIiwidmlldy1yZWFsbSIsIm1hbmFnZS1pZGVudGl0eS1wcm92aWRlcnMiLCJpbXBlcnNvbmF0aW9uIiwicmVhbG0tYWRtaW4iLCJjcmVhdGUtY2xpZW50IiwibWFuYWdlLXVzZXJzIiwicXVlcnktcmVhbG1zIiwidmlldy1hdXRob3JpemF0aW9uIiwicXVlcnktY2xpZW50cyIsInF1ZXJ5LXVzZXJzIiwibWFuYWdlLWV2ZW50cyIsIm1hbmFnZS1yZWFsbSIsInZpZXctZXZlbnRzIiwidmlldy11c2VycyIsInZpZXctY2xpZW50cyIsIm1hbmFnZS1hdXRob3JpemF0aW9uIiwibWFuYWdlLWNsaWVudHMiLCJxdWVyeS1ncm91cHMiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6IjYyOTE1Zjk3LTAxYjQtNGI2NC04ODc2LTgzMDNkOTFkYzljZSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiR2lubyBTdHJvbnppIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiZ2luby5zdHJvbnppIiwiZ2l2ZW5fbmFtZSI6Ikdpbm8iLCJmYW1pbHlfbmFtZSI6IlN0cm9uemkiLCJlbWFpbCI6Imdpbm8uc3Ryb256aUB0ZXN0LmNvbSJ9.iz466TMUD9AUlv67NHKPztx9SeUOFqpcFh0-YOL9RTE19q8BIe-onXRjUpWS2UTahf2tLFISJFGQCzefWYXsYH2k8KJJdT2bo_1bCiCu8lk0QRbglS3dpiJ1Qigb_QFULukthOeO_cpscU0TgApJTGSErsV-ChmnpVLcjtDxX4LxSnRdgi-rPjCOV6alPb21TAYylAQp4Xur-wdrx5-46Czrks1cXHMho4jWOjo-JccP1JcHz-nZpoISbKzEqHa05UXPMGVxRbjhriDgp50PggWmSxLIqdFwzr9s271HQ6efY820bSoSoWu0G9fgIHBrADARaYGU2egtVbD0OekHLw');

            $data = ['test' => $test];

            return new StatusResponse(200, true, 'ok', $data);

        } catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return  new StatusResponse(500, false, 'Internal server error');
        }
    }
}
