<?php

namespace App\Models;

class BaseModel
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