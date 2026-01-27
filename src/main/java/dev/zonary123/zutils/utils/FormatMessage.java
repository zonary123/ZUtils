package dev.zonary123.zutils.utils;

import com.hypixel.hytale.server.core.Message;
import fi.sulku.hytale.TinyMsg;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class FormatMessage {

  public static Message formatMessage(String input) {
    try {
      return TinyMsg.parse(input);
    } catch (NoSuchMethodError | NoClassDefFoundError | Exception ignored) {
      return parseLegacy(input);
    }
  }

  // --------------------------------------------------
  // Regex
  // --------------------------------------------------

  private static final Pattern PATTERN = Pattern.compile(
    "<#(?<hex>[A-Fa-f0-9]{6})>|</#>|" +
      "<gradient:#(?<g1>[A-Fa-f0-9]{6}):#(?<g2>[A-Fa-f0-9]{6})>|</gradient>|" +
      "<lang:(?<lang>[a-zA-Z0-9_.-]+)>|" +
      "[&§](?<legacy>[0-9a-fk-orA-FK-OR])"
  );

  // --------------------------------------------------
  // Parser
  // --------------------------------------------------

  private static Message parseLegacy(String input) {
    Message root = Message.empty();
    Deque<Message> stack = new ArrayDeque<>();
    stack.push(root);

    FormatState state = new FormatState();

    Matcher matcher = PATTERN.matcher(input);
    int last = 0;

    while (matcher.find()) {

      if (matcher.start() > last) {
        appendText(stack.peek(), input.substring(last, matcher.start()), state);
      }

      if (matcher.group("hex") != null) {
        Message colored = Message.empty().color("#" + matcher.group("hex"));
        stack.peek().insert(colored);
        stack.push(colored);
        state.color = "#" + matcher.group("hex");
      } else if (matcher.group(0).equals("</#>")) {
        if (stack.size() > 1) stack.pop();
      } else if (matcher.group("g1") != null) {
        state.gradientFrom = Color.decode("#" + matcher.group("g1"));
        state.gradientTo = Color.decode("#" + matcher.group("g2"));
      } else if (matcher.group(0).equals("</gradient>")) {
        state.gradientFrom = null;
        state.gradientTo = null;
      } else if (matcher.group("lang") != null) {
        stack.peek().insert(Message.translation(matcher.group("lang")));
      } else if (matcher.group("legacy") != null) {
        applyLegacy(state, matcher.group("legacy").charAt(0));
      }

      last = matcher.end();
    }

    if (last < input.length()) {
      appendText(stack.peek(), input.substring(last), state);
    }

    return root;
  }

  // --------------------------------------------------
  // Text handling
  // --------------------------------------------------

  private static void appendText(Message parent, String text, FormatState state) {
    if (text.isEmpty()) return;

    if (!state.hasGradient()) {
      parent.insert(applyState(Message.raw(text), state));
      return;
    }

    int len = text.length();
    for (int i = 0; i < len; i++) {
      float t = len == 1 ? 0f : (float) i / (len - 1);
      Color c = interpolate(state.gradientFrom, state.gradientTo, t);

      Message m = Message.raw(String.valueOf(text.charAt(i)))
        .color(toHex(c));

      parent.insert(applyState(m, state));
    }
  }

  private static Message applyState(Message msg, FormatState state) {
    if (state.color != null) msg.color(state.color);
    msg.bold(state.bold);
    msg.italic(state.italic);
    return msg;
  }

  // --------------------------------------------------
  // Legacy codes
  // --------------------------------------------------

  private static void applyLegacy(FormatState state, char c) {
    switch (Character.toLowerCase(c)) {
      case 'l' -> state.bold = true;
      case 'o' -> state.italic = true;
      case 'n' -> state.underline = true;
      case 'k' -> state.obfuscated = true;
      case 'r' -> state.reset();
      default -> state.color = getColorFromCode(c);
    }
  }

  // --------------------------------------------------
  // Utils
  // --------------------------------------------------

  private static Color interpolate(Color a, Color b, float t) {
    return new Color(
      (int) (a.getRed() + (b.getRed() - a.getRed()) * t),
      (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
      (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t)
    );
  }

  private static String toHex(Color c) {
    return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
  }

  private static String getColorFromCode(char code) {
    return switch (Character.toLowerCase(code)) {
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

  // --------------------------------------------------
  // State
  // --------------------------------------------------

  private static class FormatState {
    String color;
    boolean bold, italic, underline, obfuscated;
    Color gradientFrom, gradientTo;

    boolean hasGradient() {
      return gradientFrom != null && gradientTo != null;
    }

    void reset() {
      color = null;
      bold = italic = underline = obfuscated = false;
      gradientFrom = gradientTo = null;
    }
  }
}
