<?php

namespace App\Models;

class YamlModel
{
    /**
     * @var string
     */
    public $file;


    /**
     * BaseModel constructor.
     */
    public function __construct($file)
    {
        $this->file = $file;
    }
}
