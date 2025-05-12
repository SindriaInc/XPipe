<?php

namespace App\Http\Requests\Api;

use Illuminate\Contracts\Validation\Validator;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Http\Exceptions\HttpResponseException;

class AddAudioRequest extends FormRequest
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
    public function rules()
    {
        return [
            'name' => 'required|string|max:255',
            'description' => 'nullable|string',
            'text_type_id' => 'required|exists:text_types,id',
            'intonation_type_id' => 'required|exists:intonation_types,id',
            'voice_id' => 'required|exists:voices,id',
            'point_id' => 'required|exists:points,id',
            'service_category_id' => 'required|exists:service_categories,id',
            'gender_id' => 'required|exists:genders,id',
            'language_id' => 'required|exists:languages,id',
            'category_id' => 'required|exists:categories,id',
            'file_type_id' => 'required|exists:file_types,id',
            'post_id' => 'required|exists:wp_posts,id',
            'pronunced_text' => 'required|string',
            'platform_name' => 'nullable|string',
            'tags' => 'nullable|string',
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
