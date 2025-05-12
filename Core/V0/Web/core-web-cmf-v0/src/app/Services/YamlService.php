<?php

namespace App\Services;

use App\Models\YamlModel;
use Symfony\Component\Yaml\Yaml;
use Symfony\Component\Yaml\Exception\ParseException;
use Illuminate\Support\Facades\File;
use Illuminate\Support\Str;

abstract class YamlService
{


    /**
     * @var
     */
    protected $file;


    /**
     * @var
     */
    protected $data;


    /**
     * BaseService constructor.
     * @param YamlModel $file
     */
    public function __construct(YamlModel $file) {

        $this->file = $file;

        try {
            $this->data = Yaml::parseFile($this->file->file);
        } catch (ParseException $exception) {
            printf('Unable to parse the YAML string: %s', $exception->getMessage());
        }
    }


    /**
     * Get all file data
     *
     * @return \App\Models\YamlModel
     */
    public function all()
    {
        return $this->data;
    }


    /**
     * Update file data
     *
     * @param $validated
     * @return boolean
     */
    public function update($validated)
    {
        $yaml = Yaml::dump($validated);
        if (File::exists($this->file)) {
            unlink($this->file);
        }
        file_put_contents($this->file->file, $yaml);
        return true;
    }


    /**
     * Truncate model file
     *
     * @return mixed
     */
    public function truncate() {
        return unlink($this->file->file);
    }
}
