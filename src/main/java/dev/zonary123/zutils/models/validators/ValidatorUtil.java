package dev.zonary123.zutils.models.validators;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.zonary123.zutils.ZUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ValidatorUtil {

  private static final Cache<String, Pattern> regexCache = Caffeine.newBuilder()
    .expireAfterAccess(30, TimeUnit.MINUTES)
    .maximumSize(500)
    .build();

  private ValidatorUtil() {
  }

  public static boolean match(String value, Set<String> values) {
    if (value == null || values == null) {
      if (ZUtils.getConfig().isDebug()) {
        ZUtils.getLog().atWarning().log(
          "ValidatorUtil.match called with null value or values: value=%s, values=%s",
          value, values
        );
      }
      return false;
    }

    if (values.isEmpty() || values.contains("*")) {
      if (ZUtils.getConfig().isDebug()) {
        ZUtils.getLog().atInfo().log(
          "ValidatorUtil.match: values is empty or contains '*', returning true"
        );
      }
      return true;
    }

    if (values.contains(value)) {
      if (ZUtils.getConfig().isDebug()) {
        ZUtils.getLog().atInfo().log(
          "ValidatorUtil.match: direct match found for value '%s'", value
        );
      }
      return true;
    }

    for (String s : values) {
      if (!s.startsWith("regex:")) continue;

      String patternStr = s.substring(6);
      try {
        Pattern pattern = regexCache.get(patternStr, Pattern::compile);
        if (pattern.matcher(value).find()) {
          if (ZUtils.getConfig().isDebug()) {
            ZUtils.getLog().atInfo().log(
              "ValidatorUtil.match: regex match found for value '%s' with pattern '%s'",
              value, patternStr
            );
          }
          return true;
        }
      } catch (Exception e) {
        ZUtils.getLog().atWarning().log(
          "Invalid regex: %s", patternStr
        );
      }
    }

    return false;
  }

}
