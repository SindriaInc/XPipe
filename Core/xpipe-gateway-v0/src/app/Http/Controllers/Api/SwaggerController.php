<?php

namespace App\Http\Controllers\Api;

use Illuminate\Contracts\Routing\ResponseFactory;
use Illuminate\Http\JsonResponse;
use App\Http\Requests\Swagger\UploadSwaggerRequest;
use App\Linters\SwaggerLinter;
use App\Services\RouteService;

use Symfony\Component\Yaml\Exception\ParseException;
use Symfony\Component\Yaml\Yaml;

class SwaggerController extends ApiController
{
    /**
     * @var SwaggerLinter
     */
    protected $swaggerLinter;

    /**
     * @var RouteService
     */
    protected $routeService;

    /**
     * SwaggerController Constructor
     *
     * @param ResponseFactory $responseFactory
     * @param SwaggerLinter $swaggerLinter
     * @param RouteService $routeService
     */
    public function __construct(ResponseFactory $responseFactory, SwaggerLinter $swaggerLinter, RouteService $routeService)
    {
        parent::__construct($responseFactory);
        $this->swaggerLinter = $swaggerLinter;
        $this->routeService = $routeService;
    }

    /**
     * Upload swagger file into storage
     *
     * @param UploadSwaggerRequest $request
     * @return \Illuminate\Http\JsonResponse|\Illuminate\Http\Response
     */
    public function upload(UploadSwaggerRequest $request) : JsonResponse
    {
        try {
            $fileMimeType = $request->file('file')->getClientMimeType();

            if ($fileMimeType !== 'text/yaml') {
                return $this->sendError('validation error', 422, ['message' => 'file must be yaml']);
            }

            $fileContent = $request->file('file')->getContent();

            // Linter
            $result = $this->swaggerLinter->handle($fileContent);
            if (! $result['success']) {
                return $this->sendError($result['message'], 422);
            }

            $swagger = Yaml::parse($fileContent);
            $cachedRoutes = $this->routeService->all();

            $mergedRoutes = array_merge($swagger['routes'], $cachedRoutes['routes']);
            $mergedRoutesCollection = collect($mergedRoutes);

            $mergedRoutesDuplicates = $mergedRoutesCollection->duplicates();

            if ($mergedRoutesDuplicates->count() >= 1) {
                return $this->sendError('Found duplicate entry with routes already cached', 409, $mergedRoutesDuplicates);
            }

            $swaggerFinal = [];
            $swaggerFinal['version'] = $swagger['version'];
            $swaggerFinal['routes'] = $mergedRoutes;

            $yaml = Yaml::dump($swaggerFinal);
            file_put_contents($request->file('file'), $yaml);

            $filenameToStore = 'routes.yml';
            $request->file('file')->storeAs('cache', $filenameToStore);

            return $this->sendResponse('Swagger uploded successfully', 201);

        } catch (\Exception $e) {
            report($e);
            return $this->sendError('Internal Server Error', 500, array($e));
        }
    }

}