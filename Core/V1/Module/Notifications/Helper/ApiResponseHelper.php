<?php
namespace Core\Notifications\Helper;

class ApiResponseHelper
{
    /**
     * Success response (array-based for webapi.xml).
     */
    public static function sendSuccess(int $code = 200, string $message = 'ok', array $data = []): array
    {
        return [
            'code' => $code,
            'success' => true,
            'message' => $message,
            'data' => $data
        ];
    }

    /**
     * Error response (array-based for webapi.xml).
     */
    public static function sendError(int $code = 500, string $message = 'Error', array $data = []): array
    {
        return [
            'code' => $code,
            'success' => false,
            'message' => $message,
            'data' => $data
        ];
    }
}
