<?php
namespace Fnd\Notifications\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class NotificationsStrategyHelper
{
    public static function isBellChannel(string $channel) : bool
    {
        return $channel === 'bell';
    }

    public static function isTelegramChannel(string $channel) : bool
    {
        return $channel === 'telegram';
    }

    public static function isEmailChannel(string $email) : bool
    {
        return $email === 'email';
    }

    public static function isDiscordChannel(string $channel) : bool
    {
        return $channel === 'discord';
    }

    public static function isTeamsChannel(string $channel) : bool
    {
        return $channel === 'teams';
    }

    public static function isSlackChannel(string $channel) : bool
    {
        return $channel === 'slack';
    }

    public static function isSmsChannel(string $channel) : bool
    {
        return $channel === 'sms';
    }


    public static function isVoiceChannel(string $channel) : bool
    {
        return $channel === 'voice';
    }

    public static function isAiChannel(string $channel) : bool
    {
        return $channel === 'ai';
    }

    public static function selectDashboardRoute(string $channel) : int
    {
        if (self::isBellChannel($channel)) {
            return 0;
        } else if (self::isTelegramChannel($channel)) {
            return 1;
        } else if (self::isEmailChannel($channel)) {
            return 2;
        } else if (self::isDiscordChannel($channel)) {
            return 3;
        } else if (self::isTeamsChannel($channel)) {
            return 4;
        } else if (self::isSlackChannel($channel)) {
            return 5;
        } else if (self::isSmsChannel($channel)) {
            return 6;
        } else if (self::isVoiceChannel($channel)) {
            return 7;
        } else if (self::isAiChannel($channel)) {
            return 8;
        }

        return -1;
    }


}
