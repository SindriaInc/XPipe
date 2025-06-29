package org.sindria.xpipe.fnd.notifications;

public class Strategy {

    public static boolean isBellChannel(String channel) {
        return "bell".equals(channel);
    }

    public static boolean isTelegramChannel(String channel) {
        return "telegram".equals(channel);
    }

    public static boolean isEmailChannel(String channel) {
        return "email".equals(channel);
    }

    public static boolean isDiscordChannel(String channel) {
        return "discord".equals(channel);
    }

    public static boolean isTeamsChannel(String channel) {
        return "teams".equals(channel);
    }

    public static boolean isSlackChannel(String channel) {
        return "slack".equals(channel);
    }

    public static boolean isSmsChannel(String channel) {
        return "sms".equals(channel);
    }

    public static boolean isVoiceChannel(String channel) {
        return "voice".equals(channel);
    }

    public static boolean isAiChannel(String channel) {
        return "ai".equals(channel);
    }

    /**
     * Optimized version that use new pattern matching switch case
     * O(1)
     *
     * @param channel
     * @return int
     */
    public static int selectChannel(String channel) {
        return switch (channel) {
            case "bell"     -> 0;
            case "telegram" -> 1;
            case "email"    -> 2;
            case "discord"  -> 3;
            case "teams"    -> 4;
            case "slack"    -> 5;
            case "sms"      -> 6;
            case "voice"    -> 7;
            case "ai"       -> 8;
            default         -> -1;
        };
    }

    /**
     *
     * Legacy strategy pattern matching that use if else statements
     * O(k)
     *
     * @deprecated
     * @param channel
     * @return int
     */
    public static int selectChannelLegacy(String channel) {
        if (isBellChannel(channel)) {
            return 0;
        } else if (isTelegramChannel(channel)) {
            return 1;
        } else if (isEmailChannel(channel)) {
            return 2;
        } else if (isDiscordChannel(channel)) {
            return 3;
        } else if (isTeamsChannel(channel)) {
            return 4;
        } else if (isSlackChannel(channel)) {
            return 5;
        } else if (isSmsChannel(channel)) {
            return 6;
        } else if (isVoiceChannel(channel)) {
            return 7;
        } else if (isAiChannel(channel)) {
            return 8;
        }

        return -1;
    }
}
