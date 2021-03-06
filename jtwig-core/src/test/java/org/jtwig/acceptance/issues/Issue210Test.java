/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jtwig.acceptance.issues;

import org.jtwig.JtwigModelMap;
import org.jtwig.JtwigTemplate;
import org.junit.Test;

import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jtwig.configuration.JtwigConfigurationBuilder.newConfiguration;

/**
 *
 */
public class Issue210Test {
    @Test
    public void testNonUTFEncoding() throws Exception {
        JtwigModelMap model = new JtwigModelMap();
        model.withModelAttribute("text", "t\u00eate de bou  \uac04\ud3b8\ud55c \uc124\uce58 \ubc0f \uc0ac\uc6a9");

        String result = JtwigTemplate
            .inlineTemplate("{{ text }}", newConfiguration()
                .withCharset(Charset.forName("ISO-8859-1"))
                .build())
            .render(model);

        assertThat(result, is(equalTo("t\ufffdte de bou  ??? ?? ? ??")));
    }
    @Test
    public void testUTFEncoding() throws Exception {
        JtwigModelMap model = new JtwigModelMap();
        model.withModelAttribute("text", "t\u00eate de bou  \uac04\ud3b8\ud55c \uc124\uce58 \ubc0f \uc0ac\uc6a9");

        String result = JtwigTemplate
            .inlineTemplate("{{ text }}")
            .render(model);

        assertThat(result, is(equalTo("t\u00eate de bou  \uac04\ud3b8\ud55c \uc124\uce58 \ubc0f \uc0ac\uc6a9")));
    }
}
