<?php

namespace Pipelines\Configmap\Helper;


class ConfigmapHelper
{

    /**
     * @param string $slug
     * @param string $case
     * @return string|null
     */
    public static function makeLabelFromSlug(string $slug, string $case = 'title')
    {
        if (empty($slug)) {
            return null;
        }

        $segments = explode('-', $slug);

        // Capitalizza ogni segmento
        $segments = array_map(function ($segment) {
            return mb_convert_case($segment, MB_CASE_TITLE, "UTF-8");
        }, $segments);

        switch (strtolower($case)) {
            case 'pascal':
                // Concatenazione senza spazi
                return implode('', $segments);

            case 'camel':
                // Prima parola minuscola, le altre con iniziale maiuscola
                $first = mb_strtolower(array_shift($segments), "UTF-8");
                return $first . implode('', $segments);

            case 'title':
            default:
                // Classica label con spazi
                return implode(' ', $segments);
        }
    }


    /**
     * @param string $label
     * @return array|string|string[]|null
     */
    public static function makeSlugFromLabel(string $label)
    {
        if (empty($label)) {
            return null;
        }

        // Inserisce uno spazio prima delle lettere maiuscole che seguono una minuscola o numero
        $label = preg_replace('/([a-z0-9])([A-Z])/', '$1 $2', $label);

        // Trasforma in minuscolo e normalizza gli spazi
        $label = mb_strtolower($label, 'UTF-8');
        $label = trim(preg_replace('/\s+/', ' ', $label));

        // Rimuove caratteri non alfanumerici, esclusi gli spazi
        $label = preg_replace('/[^a-z0-9 ]/', '', $label);

        // Sostituisce gli spazi con i trattini
        $slug = str_replace(' ', '-', $label);

        return $slug;
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