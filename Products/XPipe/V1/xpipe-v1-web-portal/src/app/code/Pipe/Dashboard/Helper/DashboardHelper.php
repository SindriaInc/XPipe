<?php

namespace Pipe\Dashboard\Helper;

class DashboardHelper
{



    public static function isDevRole(string $roleName) : bool
    {
        return $roleName === 'DevRole';
    }

    public static function isIndividualRole(string $roleName) : bool
    {
        return $roleName === 'IndividualRole';
    }

    public static function isSuperAdminRole(string $roleName) : bool
    {
        return $roleName === 'Administrators';
//        return $roleName === 'SuperAdminRole';
    }

    public static function isProfileRole(string $roleName) : bool
    {
        return $roleName === 'ProfileRole';
    }

    public static function isDemoRole(string $roleName) : bool
    {
        return $roleName === 'DemoRole';
    }


    public static function selectDashboardRoute(string $roleName) : int
    {
        if (self::isSuperAdminRole($roleName)) {
            return 0;
        } else if (self::isProfileRole($roleName)) {
            return 1;
        } else if (self::isIndividualRole($roleName)) {
            return 2;
        } else if (self::isDemoRole($roleName)) {
            return 3;
        } else if (self::isDevRole($roleName)) {
            return 4;
        }

        return -1;
    }


}
