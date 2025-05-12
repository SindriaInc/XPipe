<?php

namespace App\Traits;

trait Api
{
    /**
     * Success response
     *
     * @param string $message
     * @param integer $code
     * @param array $data
     * @return \Illuminate\Http\Response|\Illuminate\Http\JsonResponse
     */
    public function sendResponse($message, $code = 200, $data = [])
    {
        $response = [
            'success' => true,
            'data'    => $data,
            'message' => $message,
        ];

        return response()->json($response, $code);
    }


    /**
     * Error response
     *
     * @param string $message
     * @param integer $code
     * @param array $errorMessages
     * @return \Illuminate\Http\Response|\Illuminate\Http\JsonResponse
     */
    public function sendError($message, $code = 404, $errorMessages = [])
    {
        $response = [
            'success' => false,
            'message' => $message,
        ];

        if (!empty($errorMessages)) {
            $response['data'] = $errorMessages;
        }
        return response()->json($response, $code);
    }
}
