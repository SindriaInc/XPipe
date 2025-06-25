<?php
namespace Pipe\PostLoginSetup\Service;

use Core\Http\Facade\HttpFacade;
use Core\Logger\Facade\LoggerFacade;
use Core\MicroFramework\Service\VaultService;
use Pipe\PostLoginSetup\Helper\PostLoginSetupHelper;

class PostLoginSetupVaultService extends VaultService
{

    public function __construct()
    {
        parent::__construct(
            PostLoginSetupHelper::getPipelinesConfigmapVaultBaseUrl(),
            PostLoginSetupHelper::getPipelinesConfigmapVaultAccessToken()
        );
    }





}
