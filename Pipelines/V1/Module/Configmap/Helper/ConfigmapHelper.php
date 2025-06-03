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

    public static function preparePayload(array $data): array {
        // Chiavi da escludere
        $excludeKeys = ['configmap_id', 'configmap_name', 'owner', 'form_key'];

        // Prepara il nuovo array con chiavi uppercase, escluse quelle da ignorare
        $payload = [];
        foreach ($data as $key => $value) {
            if (!in_array($key, $excludeKeys)) {
                $payload[strtoupper($key)] = $value;
            }
        }

        // Costruisce la struttura finale
        return ['data' => $payload];
    }


}