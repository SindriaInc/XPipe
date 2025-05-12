<?php

namespace Sindria\Toolkit;

use WP_User;

class BaseHelper
{

    /**
     * Check for capability in current user
     *
     * @param string $capability
     * @return bool
     */
    public static function hasCapability(string $capability) : bool
    {
        $userId = get_current_user_id();
        $user = new WP_User($userId);
        $currentCapabilities = $user->caps;

        foreach ($currentCapabilities as $key => $value) {
            if ($key == $capability) {
                return true;
            }
        }

        return false;
    }

    /**
     * Build add new button on top entity index page
     *
     * @param bool $hasCapability
     * @param string $url
     * @param string $text
     * @return string
     */
    public static function buildAddNewAction(bool $hasCapability, string $url, string $text) : string
    {
        return ( $hasCapability ? '<a href="'.$url.'" class="page-title-action">'.$text.'</a>' : '<a href="#" style="cursor: not-allowed;" class="page-title-action button-disabled">'.$text.'</a>' );
    }

    /**
     * Encode sequence autoincrement integer into base64
     *
     * @param int $id
     * @return string
     */
    public static function encodeSequence(int $id) : string
    {
        return base64_encode(urlencode("seq" . $id));
    }

    /**
     * Decode sequence from base64 into original autoincrement integer
     *
     * @param string $idEncoded
     * @return int
     */
    public static function decodeSequence(string $idEncoded) : int
    {
        $idDecoded = urldecode(base64_decode($idEncoded));
        return substr($idDecoded, strlen('seq'));
    }

    /**
     * Simple json beautifier
     *
     * @param string $jsonString
     * @return string
     */
    public static function jsonBeatufy(string $jsonString) : string
    {
        $json = json_encode(json_decode($jsonString), JSON_PRETTY_PRINT);
        return '<pre>' . $json . '</pre>';
    }

}
