<?php

namespace Core\MicroFramework\Action;

use Core\MicroFramework\Api\Data\StatusResponseInterface;
use Core\MicroFramework\Model\StatusResponse;
use Core\Logger\Facade\LoggerFacade;

trait ValidateAccessTokenTrait
{
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
}