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

    public static function makeSlugFromLabel(string $label)
    {
        //TODO it works only with spaces, extend validation to force spaces.
        if (strpos($label, ' ') !== false) {
            $segments = explode(' ', $label);
            $value = '';
            foreach ($segments as $segment) {
                $tmp = strtolower($segment) . '-';
                $value .=  $tmp;
            }
            return trim(substr($value, 0, -1));
        }

        return null;
    }

}