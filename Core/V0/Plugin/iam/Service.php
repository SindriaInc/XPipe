<?php

namespace Sindria\Iam;

use Sindria\Toolkit\BaseService;

class Service extends BaseService
{
    /**
     * Create user profile on local DB
     *
     * @param string $username
     * @param string $email
     * @param string $name
     * @param string $surname
     * @return int
     */
    public function createUserProfile(string $username, string $email, string $name, string $surname) : int
    {
        $userId = wp_insert_user( array(
            'user_login' => $username,
            'user_pass' => '',
            'user_nicename' => $username,
            'user_email' => $email,
            'first_name' => $name,
            'last_name' => $surname,
            'display_name' => $name . ' ' . $surname,
            'role' => 'policy'
        ));

        return $userId;
    }

    public function updateUserProfile(int $id, string $username, string $email, string $name, string $surname) : int
    {
        $userId = wp_update_user( array(
            'ID' => $id,
            'user_login' => $username,
            'user_pass' => '',
            'user_nicename' => $username,
            'user_email' => $email,
            'first_name' => $name,
            'last_name' => $surname,
            'display_name' => $name . ' ' . $surname,
            'role' => 'policy'
        ));

        return $userId;
    }


    public function deleteUserProfile(int $id) : bool
    {
        return wp_delete_user($id);
    }

    public function findProfileByUsername(string $username)
    {
        return get_user_by('login', $username);
    }


}
