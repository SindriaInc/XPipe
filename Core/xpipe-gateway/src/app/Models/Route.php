<?php
declare(strict_types=1);

namespace App\Models;

use App\Collections\RoutesCollection;

class Route extends BaseModel
{
    const METHODS = [
        'GET',
        'POST',
        'PUT',
        'DELETE',
    ];

    /**
     * @var string
     */
    public $file;

    /**
     * @var bool
     */
    public bool $isPublic;

    /**
     * @var string
     */
    public string $name;

    /**
     * @var string
     */
    public string $version;

    /**
     * @var string
     */
    public string $context;

    /**
     * @var string
     */
    public string $method;

    /**
     * @var string
     */
    public string $uri;

    /**
     * @var string
     */
    public string $comment;

    /**
     * @var string
     */
    public string $fullUri;

    /**
     * @param string $file
     * @return void
     */
    public function setFile(string $file) : void
    {
        $this->file = $file;
    }

    /**
     * @return string
     */
    public function getFile() : string
    {
        return $this->file;
    }

    /**
     * @param bool $isPublic
     * @return void
     */
    public function setIsPublic(bool $isPublic) : void
    {
        $this->isPublic = $isPublic;
    }

    /**
     * @return bool
     */
    public function getIsPublic() : bool
    {
        return $this->isPublic;
    }

    /**
     * @param string $name
     * @return void
     */
    public function setName(string $name) : void
    {
        $this->name = $name;
    }

    /**
     * @return string
     */
    public function getName() : string
    {
        return $this->name;
    }

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
     * @param string $context
     * @return void
     */
    public function setContext(string $context) : void
    {
        $this->context = $context;
    }

    /**
     * @return string
     */
    public function getContext() : string
    {
        return $this->context;
    }

    /**
     * @param string $method
     * @return void
     */
    public function setMethod(string $method) : void
    {
        $this->method = $method;
    }

    /**
     * @return string
     */
    public function getMethod() : string
    {
        return $this->method;
    }

    /**
     * @param string $uri
     * @return void
     */
    public function setUri(string $uri) : void
    {
        $this->uri = $uri;
    }

    /**
     * @return string
     */
    public function getUri() : string
    {
        return $this->uri;
    }

    /**
     * @param string $comment
     * @return void
     */
    public function setComment(string $comment) : void
    {
        $this->comment = $comment;
    }

    /**
     * @return string
     */
    public function getComment() : string
    {
        return $this->comment;
    }

    /**
     * @return void
     */
    public function setFullUri() : void
    {
        $fullUri = "api/". $this->version . "/" . $this->context . $this->uri;
        $this->fullUri = $fullUri;
    }

    /**
     * @return string
     */
    public function getFullUri() : string
    {
        return $this->fullUri;
    }


    /**
     * RouteModel constructor.
     */
    public function __construct(bool $isPublic = false, string $name = "", string $version = "", string $context = "", string $method = "", string $uri = "", string $comment = "")
    {
        $this->file = storage_path('app/cache/routes.yml');
        parent::__construct($this->file);

        $this->isPublic = $isPublic;
        $this->name = $name;
        $this->version = $version;
        $this->context = $context;
        $this->method = $method;
        $this->uri = $uri;
        $this->comment = $comment;

        $this->setFullUri();
    }

    /**
     * Create a new Eloquent Collection instance.
     *
     * @param  array  $models
     * @return RoutesCollection
     */
    public function newCollection(array $models = [])
    {
        return new RoutesCollection($models);
    }
}
