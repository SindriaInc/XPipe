<?php

namespace App\Collections;

//use Illuminate\Support\Collection;
use App\Models\Route;
use Illuminate\Database\Eloquent\Collection;

class RoutesCollection extends Collection
{
    /**
     * RoutesCollection Constructor
     *
     * @param array $routes
     */
    public function __construct($routes = [])
    {
        foreach ($routes as $entry) {
            $items[]  = new Route($entry['route']['isPublic'], $entry['route']['name'], $entry['route']['version'], $entry['route']['context'], $entry['route']['method'], $entry['route']['uri'], $entry['route']['comment']);
        }

        parent::__construct($items);
    }

//    /**
//     * Converts array items to a collections recursively.
//     * Usage: $collection = Collection::make($array)->collectArrayItems();
//     *
//     * @return mixed
//     */
//    public function collectArrayItems()
//    {
//        $this->each(function($item, $key) {
//            if (is_countable($item)) {
//                $collection = self::make($item)->collectArrayItems();
//                $this->put($key, $collection);
//            }
//        });
//        return $this;
//    }

}