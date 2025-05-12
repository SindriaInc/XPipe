<?php

namespace App\Helpers;

use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Str;

class FilesystemHelper
{
    /**
     * Check if file exists in storage path
     *
     * @param $filename
     * @param $path
     * @return bool
     */
    public static function exists($filename, $path) : bool
    {
        if (! Str::endsWith($path, "/")) {
            $path = $path . '/';
        }

        return Storage::exists($path . $filename);

    }

    /**
     * Delete a file
     *
     * @param $filepath
     * @return bool
     */
    public static function delete($filepath): bool
    {
        try {
            return Storage::delete($filepath);
        } catch (\Exception $exception) {
            return false;
        }
    }
}
