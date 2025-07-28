<?php

namespace Pipelines\Configmap\Helper;


use Core\SystemEnv\Facade\SystemEnvFacade;

class ConfigmapHelper
{

    public static function getPipelinesConfigmapVaultBaseUrl()
    {
        return SystemEnvFacade::get('PIPELINES_CONFIGMAP_VAULT_BASE_URL');
    }

    public static function getPipelinesConfigmapVaultAccessToken()
    {
        return SystemEnvFacade::get('PIPELINES_CONFIGMAP_VAULT_ACCESS_TOKEN');
    }

    public static function getIamCollectorBaseUrl()
    {
        return SystemEnvFacade::get('IAM_COLLECTOR_BASE_URL');
    }

    public static function getIamCollectorAdminUsername()
    {
        return SystemEnvFacade::get('IAM_COLLECTOR_ADMIN_USERNAME', 'carbon.user');
    }

    public static function getIamCollectorAdminPassword()
    {
        return SystemEnvFacade::get('IAM_COLLECTOR_ADMIN_PASSWORD', 'admin123');
    }

    public static function getIamUserAccessToken()
    {
        return SystemEnvFacade::get('IAM_USERS_ACCESS_TOKEN');
    }

    public static function getIamUserMetaAccessToken()
    {
        return SystemEnvFacade::get('IAM_USERS_META_ACCESS_TOKEN');
    }

    public static function getIamGroupsAccessToken()
    {
        return SystemEnvFacade::get('IAM_GROUPS_ACCESS_TOKEN');
    }

    public static function getIamPoliciesAccessToken()
    {
        return SystemEnvFacade::get('IAM_POLICIES_ACCESS_TOKEN');
    }

    /**
     * @param string $slug
     * @param string $case
     * @return string|null
     */
    public static function makeLabelFromSlug(string $slug, string $case = 'pascal')
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


    public static function preparePayload(array $data): array
    {
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


    public static function isSuperAdmin(\Magento\User\Model\User $user) : bool
    {
        return $user->getRole()->getRoleName() === "Administrators";
    }


}