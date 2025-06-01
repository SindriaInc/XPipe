<?php

namespace Pipelines\Configmap\Helper;


class ConfigmapHelper
{
    public static function makeLabelFromSlug(string $slug)
    {
        if (strpos($slug, '-') !== false) {
            $segments = explode('-', $slug);
            $value = '';
            foreach ($segments as $segment) {
                $tmp = ucfirst($segment) . ' ';
                $value .=  $tmp;
            }
            return substr($value, 0, -1);
        }

        return null;
    }

}