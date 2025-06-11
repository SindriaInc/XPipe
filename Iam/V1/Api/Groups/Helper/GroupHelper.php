<?php

namespace Iam\Groups\Helper;

class GroupHelper
{
    public static function isAll($params) : bool
    {
        if (count($params) === 0) {
            return true;
        }

        return false;
    }

    public static function isAllSearch($params) : bool
    {
        if (isset($params['q']) && empty($params['q']) && count($params) === 1) {
            return true;
        }

        return false;
    }

    public static function isSearch($params) : bool
    {
        if (isset($params['q']) && !empty($params['q']) && count($params) === 1) {
            return true;
        }

        return false;
    }

    public static function isPaginate($params) : bool
    {
        if (isset($params['off']) && !empty($params['off']) !== null && isset($params['sze']) && !empty($params['sze']) !== null && count($params) === 1) {
            return true;
        }

        return false;
    }


    public static function choosedFunction(array $params) : int
    {
        if (self::isAll($params)) {
            return 0;
        } else if (self::isAllSearch($params)) {
            return 0;
        } else if (self::isSearch($params)) {
            return 1;
        } else if (self::isPaginate($params)) {
            return 2;
        }

        return -1;
    }


}