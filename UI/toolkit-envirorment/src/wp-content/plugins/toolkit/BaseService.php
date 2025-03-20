<?php

namespace Sindria\Toolkit;

abstract class BaseService
{

    /**
     * Find user meta by userId and metaKey
     *
     * @param $userId
     * @param $metaKey
     * @return mixed|bool
     */
    public function findUserMeta($userId, $metaKey)
    {
        $hasMeta = metadata_exists( 'user', $userId, $metaKey);
        if ($hasMeta) {
            return get_user_meta($userId, $metaKey, true);
        }
        return false;
    }

    /**
     * Save or update user meta checking if already exists
     *
     * @param $userId
     * @param $metaKey
     * @param $metaValue
     * @return void
     */
    public function saveOrUpdateUserMeta($userId, $metaKey, $metaValue)
    {
        $hasMeta = metadata_exists( 'user', $userId, $metaKey);

        if ($hasMeta) {
            update_user_meta($userId, $metaKey, $metaValue);
        } else {
            add_user_meta($userId, $metaKey, $metaValue, false);
        }
    }



}
