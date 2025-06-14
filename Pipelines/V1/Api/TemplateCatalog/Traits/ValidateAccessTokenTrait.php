<?php

namespace Pipelines\TemplateCatalog\Traits;

use Core\Logger\Facade\LoggerFacade;
use Pipelines\TemplateCatalog\Model\StatusResponse;

trait ValidateAccessTokenTrait
{
    /**
     * Validate Access Token with request X-Token-XPipe header
     *
     * @param string $accessToken
     * @return StatusResponse|void
     */
    private function validateAccessToken(string $accessToken) : StatusResponse
    {
        if ($accessToken !== $this->request->getHeader('X-Token-XPipe')) {
            LoggerFacade::error('Invalid Token');
            return new StatusResponse(403, false, 'Invalid Token');
        }
    }
}