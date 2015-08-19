/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jtwig.functions.builtin;

import static java.nio.charset.Charset.forName;
import static java.util.Arrays.asList;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jtwig.functions.annotations.JtwigFunction;
import org.jtwig.functions.annotations.Parameter;
import org.jtwig.functions.exceptions.FunctionException;
import org.jtwig.functions.util.HtmlUtils;

public class StringFunctions {
    @JtwigFunction(name = "capitalize")
    public String capitalize (@Parameter String input) {
        if (input.length() > 0)
            return input.substring(0, 1).toUpperCase() + input.substring(1);
        else
            return input;
    }

    @JtwigFunction(name = "convert_encoding")
    public String convertEncoding (@Parameter String input, @Parameter String from, @Parameter String to) {
        return new String(input.getBytes(forName(from)), forName(to));
    }

    @JtwigFunction(name = "escape", aliases = {"e"})
    public Object escape (@Parameter String input) throws FunctionException {
        return new UnescaptedString(StringEscapeUtils.escapeHtml4(input));
    }

    @JtwigFunction(name = "escape", aliases = {"e"})
    public Object escape (@Parameter String input, @Parameter String strategy) throws FunctionException {
        switch (EscapeStrategy.strategyByName(strategy.toLowerCase())) {
            case JAVASCRIPT:
                return new UnescaptedString(StringEscapeUtils.escapeEcmaScript(input));
            case HTML:
            	return new UnescaptedString(StringEscapeUtils.escapeHtml4(input));
            case XML:
            default:
                return input;
        }
    }


    @JtwigFunction(name = "format")
    public String format (@Parameter String input, @Parameter Object... arguments) {
        return String.format(input, arguments);
    }

    @JtwigFunction(name = "lower")
    public String lower (@Parameter String input) {
        return input.toLowerCase();
    }

    @JtwigFunction(name = "nl2br")
    public Object nl2br (@Parameter Object input) {
    	String content;
    	if (input instanceof UnescaptedString) {
    		content = ((UnescaptedString)input).getContent();
    	}
    	else {
    		content = StringEscapeUtils.escapeXml(toTwig(input));
    	}
        return new UnescaptedString(content.replace("\n", "<br />"));
    }

    @JtwigFunction(name = "replace")
    public String replace (@Parameter String input, @Parameter Map<String, Object> replacements) {
        for (String key : replacements.keySet()) {
            if (replacements.containsKey(key)) {
                input = input.replace(key, replacements.get(key).toString());
            }
        }
        return input;
    }

    private boolean test(Map<String, Object> replacements, String key) {
        return replacements.containsKey(key);
    }

    @JtwigFunction(name = "split")
    public List<String> split (@Parameter String input, @Parameter String separator) {
        return asList(input.split(separator));
    }

    @JtwigFunction(name = "striptags")
    public Object stripTags (@Parameter String input) {
        return stripTags(input, "");
    }

    @JtwigFunction(name = "striptags")
    public Object stripTags (@Parameter String input, @Parameter String allowedTags) {
        return new UnescaptedString( HtmlUtils.stripTags(input, allowedTags));
    }


    @JtwigFunction(name = "title")
    public String title (@Parameter String input) {
        return WordUtils.capitalize(input);
    }

    @JtwigFunction(name = "trim")
    public String trim (@Parameter String input) {
        return (input == null) ? null : input.trim();
    }

    @JtwigFunction(name = "upper")
    public String upper (@Parameter String input) {
        return input.toUpperCase();
    }

    @JtwigFunction(name = "url_encode")
    public String urlEncode (@Parameter String input) throws UnsupportedEncodingException {
        return URLEncoder.encode(input, Charset.defaultCharset().displayName());
    }
    @JtwigFunction(name = "url_encode")
    public String urlEncode (@Parameter Map input) throws UnsupportedEncodingException {
        List<String> pieces = new ArrayList<String>();
        for (Object key : input.keySet()) {
            pieces.add(urlEncode(key.toString()) + "=" + urlEncode(input.get(key).toString()));
        }
        return StringUtils.join(pieces, "&");
    }

    @JtwigFunction(name = "first")
    public Character first (@Parameter String input) {
        if (input.isEmpty()) return null;
        return input.charAt(0);
    }
    @JtwigFunction(name = "last")
    public Character last (@Parameter String input) {
        if (input.isEmpty()) return null;
        return input.charAt(input.length() - 1);
    }
    @JtwigFunction(name = "reverse")
    public String reverse (@Parameter String input) {
        return new StringBuilder(input).reverse().toString();
    }

    /**
     * This function does output the content unescaped.
     *
     * @param test the object to handle without escaping.
     * @return
     */
	@JtwigFunction(name= "raw")
	public UnescaptedString raw(@Parameter Object test) {
		return new UnescaptedString(toTwig(test));
	}


    private static String toTwig (Object object) {
        if (object instanceof Boolean)
            return ((boolean)object) ? "1" : "0";

        return String.valueOf(object);
    }



    enum EscapeStrategy {
        HTML("html"),
        JAVASCRIPT("js", "javascript"),
        XML("xml");

        private List<String> representations;

        EscapeStrategy(String... representations) {
            this.representations = asList(representations);
        }

        public static EscapeStrategy strategyByName(String name) {
            for (EscapeStrategy escape : EscapeStrategy.values()) {
                if (escape.representations.contains(name))
                    return escape;
            }
            throw new IllegalStateException(String.format("Unknown strategy '%s'", name));
        }
    }

}
