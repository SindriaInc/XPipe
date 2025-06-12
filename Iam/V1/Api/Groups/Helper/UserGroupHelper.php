<?php

namespace Iam\Groups\Helper;

class UserGroupHelper
{
    public static function isPayloadValid(array $payload): bool
    {
        return count($payload) === 2 && isset($payload['username']) && isset($payload['group_slug']);
    }

}