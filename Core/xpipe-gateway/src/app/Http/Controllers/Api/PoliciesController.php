<?php
declare(strict_types=1);

namespace App\Http\Controllers\Api;

use App\Http\Requests\Policies\SearchPolicyRequest;
use Illuminate\Contracts\Routing\ResponseFactory;
use App\Http\Requests\Policies\AddPolicyRequest;
use App\Http\Requests\Policies\EditPolicyRequest;
use App\Http\Requests\Policies\AttachPolicyRequest;
use App\Http\Requests\Policies\DetachPolicyRequest;
use Illuminate\Support\Facades\Http;

class PoliciesController extends ApiController
{

    /**
     * @var string $service
     */
    private string $serviceUrl;


    /**
     * PoliciesController constructor.
     *
     * @param ResponseFactory $responseFactory
     */
    public function __construct(ResponseFactory $responseFactory)
    {
        parent::__construct($responseFactory);
        $this->serviceUrl = env('POLICIES_SERVICE');
    }


    /**
     * Get all policies
     *
     * @return \Illuminate\Http\JsonResponse
     */
    public function index()
    {
        $url = $this->serviceUrl .'/api/v1/policies';
        $response = Http::get($url);
        return $response->json();
    }


    /**
     * Show a policy
     *
     * @param integer $id
     * @return \Illuminate\Http\JsonResponse
     */
    public function show(int $id)
    {
        $url = $this->serviceUrl .'/api/v1/policies/' . $id;
        $response = Http::get($url);
        return $response->json();
    }


    /**
     * Store a policy
     *
     * @param AddPolicyRequest $request
     * @return \Illuminate\Http\JsonResponse
     */
    public function store(AddPolicyRequest $request)
    {
        $validated = $request->validated();
        $url = $this->serviceUrl .'/api/v1/policies';
        $payload = json_encode($validated);
        $response = Http::withBody($payload, 'application/json')->post($url);
        return $response->json();
    }


    /**
     * Edit a policy
     *
     * @param EditPolicyRequest $request
     * @return \Illuminate\Http\JsonResponse
     */
    public function edit(EditPolicyRequest $request)
    {
        $validated = $request->validated();
        $url = $this->serviceUrl .'/api/v1/policies/' . $validated['id'];
        $payload = json_encode($validated);
        $response = Http::withBody($payload, 'application/json')->put($url);
        return $response->json();
    }


    /**
     * Delete a policy
     *
     * @param integer $id
     * @return \Illuminate\Http\JsonResponse
     */
    public function delete(int $id)
    {
        $url = $this->serviceUrl .'/api/v1/policies/' . $id;
        $response = Http::delete($url);
        return $response->json();
    }


    /**
     * Search policy
     *
     * @param SearchPolicyRequest $request
     * @return \Illuminate\Http\JsonResponse
     */
    public function search(SearchPolicyRequest $request)
    {
        $validated = $request->validated();
        $url = $this->serviceUrl .'/api/v1/policies/search?q=' . $validated['query'];
        $response = Http::get($url);
        return $response->json();
    }


    /**
     * Show policies attached to user
     *
     * @param string $id
     * @return \Illuminate\Http\JsonResponse
     */
    public function showUserPolicies($id)
    {
        $url = $this->serviceUrl .'/api/v1/policies/user/' . $id;
        $response = Http::get($url);
        return $response->json();
    }


    /**
     * Show users attached to policy
     *
     * @param int $id
     * @return \Illuminate\Http\JsonResponse
     */
    public function showPolicyUsers($id)
    {
        $url = $this->serviceUrl .'/api/v1/policies/users/' . $id;
        $response = Http::get($url);
        return $response->json();
    }


    /**
     * Attach policy to user
     *
     * @param AttachPolicyRequest $request
     * @return array|mixed
     */
    public function attach(AttachPolicyRequest $request)
    {
        $validated = $request->validated();

        $input = [];
        $input['userId'] = $validated['user_id'];
        $input['policyId'] = $validated['policy_id'];

        $url = $this->serviceUrl .'/api/v1/policies/attach';
        $payload = json_encode($input);
        $response = Http::withBody($payload, 'application/json')->post($url);
        return $response->json();
    }


    /**
     * Detach policy from user
     *
     * @param DetachPolicyRequest $request
     * @return array|mixed
     */
    public function detach(DetachPolicyRequest $request)
    {
        $validated = $request->validated();

        $input = [];
        $input['userId'] = $validated['user_id'];
        $input['policyId'] = $validated['policy_id'];

        $url = $this->serviceUrl .'/api/v1/policies/detach';
        $payload = json_encode($input);
        $response = Http::withBody($payload, 'application/json')->post($url);
        return $response->json();
    }
}