package dev.zonary123.zutils.utils;

import com.hypixel.hytale.server.core.Message;
import fi.sulku.hytale.TinyMsg;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class FormatMessage {
  public static Message formatMessage(String input) {
    Message message;
    try {
      message = TinyMsg.parse(input);
    } catch (NoSuchMethodError | NoClassDefFoundError e) {
      // Fallback for older Hytale versions without TinyMsg
      message = f(input);
    }
    return message;
  }

  private static final Pattern PATTERN = Pattern.compile(
    "<#(?<hex>[A-Fa-f0-9]{6})>|</#>|" +
      "<gradient:#(?<g1>[A-Fa-f0-9]{6}):#(?<g2>[A-Fa-f0-9]{6})>|</gradient>|" +
      "<lang:(?<lang>[a-zA-Z0-9_.-]+)>|" +
      "[&§](?<legacy>[0-9a-fk-orA-FK-OR])"
  );


  private static Message f(String input) {
    Message root = Message.empty();
    Message current = root;

    Color gradientFrom = null;
    Color gradientTo = null;

    Matcher matcher = PATTERN.matcher(input);
    int lastIndex = 0;

    while (matcher.find()) {

      // Texto plano
      if (matcher.start() > lastIndex) {
        appendText(
          current,
          input.substring(lastIndex, matcher.start()),
          gradientFrom,
          gradientTo
        );
      }

      // <#HEX>
      if (matcher.group("hex") != null) {
        current = Message.empty().color("#" + matcher.group("hex"));
        root.insert(current);
      }

      // </#>
      else if (matcher.group(0).equals("</#>")) {
        current = root;
      }

      // <gradient:#A:#B>
      else if (matcher.group("g1") != null) {
        gradientFrom = Color.decode("#" + matcher.group("g1"));
        gradientTo = Color.decode("#" + matcher.group("g2"));
      }

      // </gradient>
      else if (matcher.group(0).equals("</gradient>")) {
        gradientFrom = null;
        gradientTo = null;
      }

      // <lang:key>
      else if (matcher.group("lang") != null) {
        current.insert(Message.translation(matcher.group("lang")));
      }

      // & / § legacy
      else if (matcher.group("legacy") != null) {
        applyLegacy(current, matcher.group("legacy").charAt(0));
      }

      lastIndex = matcher.end();
    }

    // Texto restante
    if (lastIndex < input.length()) {
      appendText(
        current,
        input.substring(lastIndex),
        gradientFrom,
        gradientTo
      );
    }

    return root;
  }

  // -----------------------
  // Helpers
  // -----------------------

  private static void appendText(Message parent, String text, Color from, Color to) {
    if (text.isEmpty()) return;

    if (from == null) {
      parent.insert(Message.raw(text));
      return;
    }

    int len = text.length();
    for (int i = 0; i < len; i++) {
      float ratio = len == 1 ? 0f : (float) i / (len - 1);
      Color c = interpolate(from, to, ratio);

      Message m = Message.empty()
        .color(String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue()));

      m.insert(Message.raw(String.valueOf(text.charAt(i))));
      parent.insert(m);
    }
  }

  private static Color interpolate(Color from, Color to, float ratio) {
    int r = (int) (from.getRed() + (to.getRed() - from.getRed()) * ratio);
    int g = (int) (from.getGreen() + (to.getGreen() - from.getGreen()) * ratio);
    int b = (int) (from.getBlue() + (to.getBlue() - from.getBlue()) * ratio);
    return new Color(r, g, b);
  }

  private static void applyLegacy(Message msg, char c) {
    char code = Character.toLowerCase(c);

    switch (code) {
      case 'l', 'b' -> msg.bold(true);
      case 'o' -> msg.italic(true);
      case 'r' -> {
        msg.bold(false);
        msg.italic(false);
        msg.color("#FFFFFF");
      }
      default -> msg.color(getColorFromCode(code));
    }
  }

  private static String getColorFromCode(char code) {
    return switch (code) {
      case '0' -> "#000000";
      case '1' -> "#0000AA";
      case '2' -> "#00AA00";
      case '3' -> "#00AAAA";
      case '4' -> "#AA0000";
      case '5' -> "#AA00AA";
      case '6' -> "#FFAA00";
      case '7' -> "#AAAAAA";
      case '8' -> "#555555";
      case '9' -> "#5555FF";
      case 'a' -> "#55FF55";
      case 'b' -> "#55FFFF";
      case 'c' -> "#FF5555";
      case 'd' -> "#FF55FF";
      case 'e' -> "#FFFF55";
      case 'f' -> "#FFFFFF";
      default -> "#FFFFFF";
    };
  }
}
