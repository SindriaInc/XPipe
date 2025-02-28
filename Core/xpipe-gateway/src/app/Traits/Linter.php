<?php
declare(strict_types=1);

namespace App\Traits;

trait Linter
{
    /**
     * Success response
     *
     * @param string $message
     * @return array
     */
    public function sendResponse(string $message = 'ok') : array
    {
        $response = [
            'success' => true,
            'message' => $message,
        ];

        return $response;
    }


    /**
     * Error response
     *
     * @param int $line
     * @param string $snippet
     * @param string $message
     * @return array
     */
    public function sendError(int $line, string $snippet, string $message) : array
    {
        $response = [
            'success' => false,
            'line' => $line,
            'snippet' => $snippet,
            'message' => $message,
        ];

        return $response;
    }
}