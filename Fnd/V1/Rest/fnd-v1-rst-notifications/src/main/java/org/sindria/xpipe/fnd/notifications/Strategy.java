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

    public static int selectChannel(String channel) {
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
