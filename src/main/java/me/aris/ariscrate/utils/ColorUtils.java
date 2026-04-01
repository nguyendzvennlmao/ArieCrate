package me.aris.ariscrate.utils;

import net.md_5.bungee.api.ChatColor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {
    private static final Pattern HEX = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String color(String msg) {
        if (msg == null) return "";
        Matcher m = HEX.matcher(msg);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            m.appendReplacement(sb, ChatColor.of("#" + m.group(1)).toString());
        }
        return ChatColor.translateAlternateColorCodes('&', m.appendTail(sb).toString());
    }
}
