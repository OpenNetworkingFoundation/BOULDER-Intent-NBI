package org.openintent.devkit;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ScriptableObject;

import com.iaasframework.devkit.RhinoUtils;



/**
 * Direct conversion between native Rhino objects and JSON.
 * <p>
 * This class can be used directly in Rhino.
 * <p>
 * This class was inspired from {@link http
 * ://mongodb-rhino.googlecode.com/svn/trunk/modules/com.mongodb.rhino/src/com/mongodb/rhino/JSON.java}
 *
 * @author Alex Objelean
 * @since 1.3.6
 */
public class RhinoUtils {

  /**
   * Creates a more detailed message based on {@link RhinoException} thrown by rhino execution. The message will contain
   * a detailed description of the problem by inspecting the JSON value provided by exception.
   *
   * @param e {@link RhinoException} thrown by rhino execution.
   * @return detailed string message.
   */
  public static String createExceptionMessage(final RhinoException e) {
    String message = "Could not execute the script because: ";
    if (e instanceof JavaScriptException) {
      message += toJson(((JavaScriptException) e).getValue());
    } else {
      message += e.getMessage();
    }
    return message;
  }

  /**
   * Recursively convert from native Rhino to JSON.
   * <p>
   * Recognizes JavaScript objects, arrays and primitives.
   * <p>
   * Special support for JavaScript dates: converts to {"$date": timestamp} in JSON.
   * <p>
   * Special support for MongoDB ObjectId: converts to {"$oid": "objectid"} in JSON.
   * <p>
   * Also recognizes JVM types: java.util.Map, java.util.Collection, java.util.Date.
   *
   * @param object
   *          A Rhino native object
   * @return The JSON string
   * @see RhinoUtils#convertSpecial(Object)
   */
  public static String toJson(final Object object) {
    return toJson(object, false);
  }

  /**
   * Recursively convert from native Rhino to JSON.
   * <p>
   * Recognizes JavaScript objects, arrays and primitives.
   * <p>
   * Special support for JavaScript dates: converts to {"$date": timestamp} in JSON.
   * <p>
   * Special support for MongoDB ObjectId: converts to {"$oid": "objectid"} in JSON.
   * <p>
   * Also recognizes JVM types: java.util.Map, java.util.Collection, java.util.Date.
   *
   * @param object
   *          A Rhino native object
   * @param indent
   *          Whether to indent the JSON for human readability
   * @return The JSON string
   */
  public static String toJson(final Object object, final boolean indent) {
    final StringBuilder s = new StringBuilder();
    encode(s, object, indent, indent ? 0 : -1);
    return s.toString();
  }

  private static void encode(final StringBuilder s, final Object object, final boolean indent, final int depth) {
    if (indent)
      indent(s, depth);

    if (object == null) {
      s.append("null");
    } else if (object instanceof Number) {
      s.append(object);
    } else if (object instanceof Boolean) {
      s.append(object);
    } else if (object instanceof Date) {
      final HashMap<String, Long> map = new HashMap<String, Long>();
      map.put("$date", ((Date) object).getTime());
      encode(s, map, depth);
    } else if (object instanceof Collection) {
      encode(s, (Collection<?>) object, depth);
    } else if (object instanceof Map) {
      encode(s, (Map<?, ?>) object, depth);
  //  } else if (object instanceof NativeArray) {
   //   encode(s, (NativeArray) object, depth);
    } 
    else if (object instanceof ScriptableObject) {
      final ScriptableObject scriptable = (ScriptableObject) object;
      if (scriptable.getClassName().equals("Date")) {
        // (The NativeDate class is private in Rhino, but we can access
        // it like a regular object.)

        final Object time = ScriptableObject.callMethod(scriptable, "getTime", null);
        if (time instanceof Number) {
          encode(s, new Date(((Number) time).longValue()), false, depth);
          return;
        }
      }

      encode(s, scriptable, depth);
    } else {
      s.append('\"');
      s.append(escape(object.toString()));
      s.append('\"');
    }
  }

  private static void encode(final StringBuilder s, final Collection<?> collection, final int depth) {
    s.append('[');

    final Iterator<?> i = collection.iterator();
    if (i.hasNext()) {
      if (depth > -1)
        s.append('\n');

      while (true) {
        final Object value = i.next();

        encode(s, value, true, depth > -1 ? depth + 1 : -1);

        if (i.hasNext()) {
          s.append(',');
          if (depth > -1)
            s.append('\n');
        } else
          break;
      }

      if (depth > -1)
        s.append('\n');
    }

    if (depth > -1)
      indent(s, depth);
    s.append(']');
  }

  private static void encode(final StringBuilder s, final Map<?, ?> map, final int depth) {
    s.append('{');

    final Iterator<?> i = map.entrySet().iterator();
    if (i.hasNext()) {
      if (depth > -1)
        s.append('\n');

      while (true) {
        final Map.Entry<?, ?> entry = (Map.Entry<?, ?>) i.next();
        final String key = entry.getKey().toString();
        final Object value = entry.getValue();

        if (depth > -1)
          indent(s, depth + 1);

        s.append('\"');
        s.append(escape(key));
        s.append("\":");

        if (depth > -1)
          s.append(' ');

        encode(s, value, false, depth > -1 ? depth + 1 : -1);

        if (i.hasNext()) {
          s.append(',');
          if (depth > -1)
            s.append('\n');
        } else
          break;
      }

      if (depth > -1)
        s.append('\n');
    }

    if (depth > -1)
      indent(s, depth);
    s.append('}');
  }

  @SuppressWarnings("unused")
private static void encode(final StringBuilder s, final NativeArray array, final int depth) {
    s.append('[');

    final long length = array.getLength();
    if (length > 0) {
      if (depth > -1)
        s.append('\n');

      for (int i = 0; i < length; i++) {
        final Object value = ScriptableObject.getProperty(array, i);

        encode(s, value, true, depth > -1 ? depth + 1 : -1);

        if (i < length - 1) {
          s.append(',');
          if (depth > -1)
            s.append('\n');
        }
      }

      if (depth > -1)
        s.append('\n');
    }

    if (depth > -1)
      indent(s, depth);
    s.append(']');
  }

  private static void encode(final StringBuilder s, final ScriptableObject object, final int depth) {
    s.append('{');

    final Object[] ids = object.getAllIds();
    if (ids.length > 0) {
      if (depth > -1)
        s.append('\n');

      final int length = ids.length;
      for (int i = 0; i < length; i++) {
        final String key = ids[i].toString();
        final Object value = ScriptableObject.getProperty(object, key);

        if (depth > -1)
          indent(s, depth + 1);

        s.append('\"');
        s.append(escape(key));
        s.append("\":");

        if (depth > -1)
          s.append(' ');

        encode(s, value, false, depth > -1 ? depth + 1 : -1);

        if (i < length - 1) {
          s.append(',');
          if (depth > -1)
            s.append('\n');
        }
      }

      if (depth > -1)
        s.append('\n');
    }

    if (depth > -1)
      indent(s, depth);
    s.append('}');
  }

  private static void indent(final StringBuilder s, final int depth) {
    for (int i = depth - 1; i >= 0; i--)
      s.append("  ");
  }

  private static Pattern[] ESCAPE_PATTERNS = new Pattern[] {
    Pattern.compile("\\\\"), Pattern.compile("\\n"), Pattern.compile("\\r"), Pattern.compile("\\t"),
    Pattern.compile("\\f"), Pattern.compile("\\\"")
  };

  private static String[] ESCAPE_REPLACEMENTS = new String[] {
    Matcher.quoteReplacement("\\\\"), Matcher.quoteReplacement("\\n"), Matcher.quoteReplacement("\\r"),
    Matcher.quoteReplacement("\\t"), Matcher.quoteReplacement("\\f"), Matcher.quoteReplacement("\\\"")
  };

  private static String escape(String string) {
    for (int i = 0, length = ESCAPE_PATTERNS.length; i < length; i++)
      string = ESCAPE_PATTERNS[i].matcher(string).replaceAll(ESCAPE_REPLACEMENTS[i]);
    return string;
  }
}