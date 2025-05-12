<?php

namespace App\Services;

use App\Models\Route;
use Illuminate\Support\Collection;

class RouteService extends BaseService
{

    /**
     * @var string
     */
    protected $file;


    /**
     * RouteService constructor.
     */
    public function __construct(Route $file)
    {
        parent::__construct($file);
    }

    /**
     * Fetch all cached routes
     *
     * @return Collection
     */
    public function fetchAllRoutes(): Collection
    {
        $data = $this->all();

        $collection = [];

        foreach ($data['routes'] as $entry) {
            $collection[] = new Route($entry['route']['isPublic'], $entry['route']['name'], $entry['route']['version'], $entry['route']['context'], $entry['route']['method'], $entry['route']['uri'], $entry['route']['comment']);
        }

        return collect($collection);
    }


    /**
     * Fetch only public routes
     *
     * @return Collection
     */
    public function fetchPublicRoutes(): Collection
    {
        $collection = [];
        $routes = $this->fetchAllRoutes()->getPublicRoutes();

        foreach ($routes as $key => $value) {
            if (! $value) {
                unset($routes[$key]);
            } else {
                $collection[] = $value;
            }
        }

        return collect($collection);
    }

    /**
     * Fetch only private routes
     *
     * @return Collection
     */
    public function fetchPrivateRoutes(): Collection
    {
        $collection = [];
        $routes = $this->fetchAllRoutes()->getPrivateRoutes();


        foreach ($routes as $key => $value) {
            if (! $value) {
                unset($routes[$key]);
            } else {
                $collection[] = $value;
            }
        }

        return collect($collection);
    }

    /**
     * Store generated routes into php file
     *
     * @param string $content
     * @return bool
     */
    public function storeGeneratedRoutes(string $content, string $filename = "generated_routes.php" ) : bool
    {
        try {

            if (!file_exists(storage_path('app/generated/'))) {
                mkdir(storage_path('app/generated/'), 0755, true);
            }

            $file = storage_path('app/generated/'.$filename);
            $fileHandle = fopen($file, 'w');
            fwrite($fileHandle, $content);
            fclose($fileHandle);
            return true;

        } catch (\Exception $e) {
            return false;
        }
    }






}