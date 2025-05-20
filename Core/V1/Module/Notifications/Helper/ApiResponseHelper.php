<?php
namespace Core\Notifications\Helper;

use Magento\Framework\Controller\Result\JsonFactory;
use Magento\Framework\Controller\Result\Json;
use Magento\Framework\Webapi\Exception as WebapiException;

class ApiResponseHelper
{
    /**
     * Mappa costanti di WebapiException a codici HTTP.
     */
    private const ERROR_CODES = [
        WebapiException::HTTP_BAD_REQUEST => 400,
        WebapiException::HTTP_UNAUTHORIZED => 401,
        WebapiException::HTTP_FORBIDDEN => 403,
        WebapiException::HTTP_NOT_FOUND => 404,
        WebapiException::HTTP_INTERNAL_ERROR => 500,
    ];

    /**
     * Success response
     */
    public static function sendSuccess(JsonFactory $jsonFactory, int $code, string $message = 'ok', array $data = []): Json
    {
        $response = $jsonFactory->create();
        return $response->setData([
            'code' => $code,
            'success' => true,
            'message' => $message,
            'data' => $data
        ]);
    }

    /**
     * Error response
     */
    public static function sendError(int $code, string $message = 'Error', array $data = []): WebapiException
    {
        $mappedCode = self::ERROR_CODES[$code] ?? WebapiException::HTTP_INTERNAL_ERROR;

        return new WebapiException(
            __($message),
            0,
            $mappedCode,
            [],
            [
                'code' => $code,
                'success' => false,
                'message' => $message,
                'data' => $data
            ]
        );
    }
}
