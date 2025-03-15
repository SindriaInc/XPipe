<?php
declare(strict_types=1);

namespace App\Models;

use App\Collections\RoutesCollection;

class Swagger extends BaseModel
{

    /**
     * @var string
     */
    public $file;

    /**
     * @var string
     */
    public $version;

    /**
     * @var array
     */
    public $routes;

    /**
     * @param string $version
     * @return void
     */
    public function setVersion(string $version) : void
    {
        $this->version = $version;
    }

    /**
     * @return string
     */
    public function getVersion() : string
    {
        return $this->version;
    }


    /**
     * @param RoutesCollection $routes
     * @return void
     */
    public function setRoutes(RoutesCollection $routes) : void
    {
        $this->routes = $routes;
    }

    /**
     * @return RoutesCollection
     */
    public function getRoutes()
    {
        return $this->routes;
    }

    /**
     * SwaggerModel constructor.
     */
    public function __construct(string $version, array $routes)
    {
        parent::__construct($this->file);

        $this->version = $version;
        $this->routes = new RoutesCollection($routes);
    }







}