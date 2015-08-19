package org.jtwig.acceptance;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.ArrayList;
import java.util.List;

import org.jtwig.JtwigModelMap;
import org.jtwig.JtwigTemplate;
import org.jtwig.configuration.JtwigConfiguration;
import org.jtwig.configuration.JtwigConfigurationBuilder;
import org.jtwig.exception.JtwigException;
import org.jtwig.functions.annotations.JtwigFunction;
import org.jtwig.functions.annotations.Parameter;
import org.junit.Test;

public class XSSTest {

	@Test
	public void testXSSEscapting() throws JtwigException {
		JtwigModelMap model = new JtwigModelMap();

		model.put("content", "<p>test</p>");

		String result = JtwigTemplate.classpathTemplate("templates/acceptance/include/xss.twig").render(model);

		assertThat(result, is(equalTo("Some test with variable &lt;p&gt;test&lt;/p&gt;.")));
	}

	@Test
	public void testXSSComplex() throws JtwigException {
		JtwigModelMap model = new JtwigModelMap();

		TestObject var = new TestObject();
		var.name = "<p>test</p>";
		var.list.add("<div>some</div>");
		var.list.add("some2");
		model.put("content", var);

		JtwigConfiguration configuration = JtwigConfigurationBuilder.newConfiguration().build();
		configuration.getFunctionRepository().include(new TestFunctions());

		JtwigTemplate renderer = JtwigTemplate.classpathTemplate("templates/acceptance/include/xss-function.twig",
				configuration);
		String result = renderer.render(model);

		assertThat(result, is(equalTo("Some test with variable &lt;p&gt;test&lt;/p&gt;&lt;div&gt;some&lt;/div&gt;.")));
	}

	@Test
	public void testXSSRaw() throws JtwigException {
		JtwigModelMap model = new JtwigModelMap();

		TestObject var = new TestObject();
		var.name = "<p>test</p>";
		var.list.add("<div>some</div>");
		var.list.add("some2");
		model.put("content", var);

		JtwigConfiguration configuration = JtwigConfigurationBuilder.newConfiguration().build();
		configuration.getFunctionRepository().include(new TestFunctions());

		JtwigTemplate renderer = JtwigTemplate.classpathTemplate("templates/acceptance/include/xss-raw.twig",
				configuration);
		String result = renderer.render(model);

		assertThat(result, is(equalTo("Some test with variable <p>test</p><div>some</div>.")));
	}

	static class TestObject {

		private String name;

		private List<String> list = new ArrayList<String>();

		@Override
		public String toString() {
			return this.name + this.list.get(0);
		}

	}

	public static class TestFunctions {

		@JtwigFunction(name = "map")
		public TestObject map(@Parameter TestObject test) {
			return test;
		}
	}

}
