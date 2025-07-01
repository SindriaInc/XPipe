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

            $test = $this->userService->loggedUser('.eyJleHAiOjE3NTEzNzM1NzcsImlhdCI6MTc1MTM3MzI3NywianRpIjoiODI2YThkZjAtYzljYy00ZjRjLTk5MjktMzk2ZWJhMWE4MTVkIiwiaXNzIjoiaHR0cHM6Ly9kZXYtYXV0aC14cGlwZS5zaW5kcmlhLm9yZy9hdXRoL3JlYWxtcy9zaW5kcmlhIiwiYXVkIjpbInJlYWxtLW1hbmFnZW1lbnQiLCJhY2NvdW50Il0sInN1YiI6ImIyMDMzY2VhLTJlYTYtNGFjZC05Yzc1LTJhMmZkYzYzOGJkOSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImRldi14cGlwZS1jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiY2Y2ZjRmZGYtMzkwYS00YmIwLTljMTMtNWVmYWFhNDcyNTUzIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL2Rldi1kYXNoYm9hcmQteHBpcGUuc2luZHJpYS5vcmciXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtc2luZHJpYSIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJyZWFsbS1tYW5hZ2VtZW50Ijp7InJvbGVzIjpbInZpZXctaWRlbnRpdHktcHJvdmlkZXJzIiwidmlldy1yZWFsbSIsIm1hbmFnZS1pZGVudGl0eS1wcm92aWRlcnMiLCJpbXBlcnNvbmF0aW9uIiwicmVhbG0tYWRtaW4iLCJjcmVhdGUtY2xpZW50IiwibWFuYWdlLXVzZXJzIiwicXVlcnktcmVhbG1zIiwidmlldy1hdXRob3JpemF0aW9uIiwicXVlcnktY2xpZW50cyIsInF1ZXJ5LXVzZXJzIiwibWFuYWdlLWV2ZW50cyIsIm1hbmFnZS1yZWFsbSIsInZpZXctZXZlbnRzIiwidmlldy11c2VycyIsInZpZXctY2xpZW50cyIsIm1hbmFnZS1hdXRob3JpemF0aW9uIiwibWFuYWdlLWNsaWVudHMiLCJxdWVyeS1ncm91cHMiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6ImNmNmY0ZmRmLTM5MGEtNGJiMC05YzEzLTVlZmFhYTQ3MjU1MyIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiR2lubyBTdHJvbnppIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiZ2luby5zdHJvbnppIiwiZ2l2ZW5fbmFtZSI6Ikdpbm8iLCJmYW1pbHlfbmFtZSI6IlN0cm9uemkiLCJlbWFpbCI6Imdpbm8uc3Ryb256aUB0ZXN0LmNvbSJ9.tj0iXbHwxsM-t6_R7G1oSwmQQzfdu7P1UqTUVgnLPUqBy_ua0mc4g5_WoxSpo2dhdEbzZhU1_03D4M7wHSvrTEz4gozUfdp54_TvvYzLh5pA0W6Lilcn3EoEtaI-ZBZhnNYXjRNwi1pQ47oMRecWxJLnon7NhnIpPtUjBbBjEURt29y4RIOMaLC6XFc7ph8iaZjbkYmFA0OXjlrhAtcy7ZhFSIrl8ooYB3ycory4rFk_0gdnh8OnClvrMxguzd_55WIwiVs31tILk29xh7c7nJjJaCTbsc6mFbamSS3GtGg4P6Rje-q94Tsz-F3JgeNGEy9lc7aqlJV8bSZI1TSRjQ');
            $data = ['test' => $test];

            return new StatusResponse(200, true, 'ok', $data);

        } catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return  new StatusResponse(500, false, 'Internal server error');
        }
    }
}
