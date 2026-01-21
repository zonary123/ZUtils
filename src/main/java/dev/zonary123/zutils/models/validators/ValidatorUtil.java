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
    boolean matched = false;
    // Direct match or wildcard
    matched |= values.isEmpty() || values.contains("*") || values.contains(value);
    if (matched) {
      if (ZUtils.getConfig().isDebug()) {
        ZUtils.getLog().atInfo().log(
          "ValidatorUtil.match: value='%s' matched directly", value
        );
      }
      return true;
    }

    // Regex match
    for (String s : values) {
      if (s.startsWith("regex:")) {
        String patternStr = s.substring(6);
        Pattern pattern = regexCache.get(patternStr, Pattern::compile);
        if (pattern.matcher(value).matches()) {
          matched = true;
          break;
        }
      }
    }

    if (ZUtils.getConfig().isDebug()) {
      ZUtils.getLog().atInfo().log(
        "ValidatorUtil.match: value='%s' matched='%s' via regex", value, matched
      );
    }
    return matched;
  }
}
