<?php

namespace App\Http\Requests\Policies;

use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Contracts\Validation\Validator;
use Illuminate\Http\Exceptions\HttpResponseException;

class DetachPolicyRequest extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     *
     * @return bool
     */
    public function authorize()
    {
        return true;
    }

    /**
     * Get the validation rules that apply to the request.
     *
     * @return array
     */
    public function rules() : array
    {
        return [
            'user_id' => '',
            'policy_id' => '',
        ];
    }


    /**
     * Restful validation messages
     *
     * @param Validator $validator
     */
    protected function failedValidation(Validator $validator)
    {
        $response = [
            'success' => false,
            'message' => 'validation error',
            'data'    => ['messages' => $validator->errors()]
        ];

        throw new HttpResponseException(response()->json($response, 422));
    }
}
