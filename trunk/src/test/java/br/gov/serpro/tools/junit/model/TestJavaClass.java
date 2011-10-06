package br.gov.serpro.tools.junit.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Testes para a classe {@link JavaClass}.
 */
public class TestJavaClass {

	private JavaClass javaClass;

	@Before
	public void before() {
		javaClass = new JavaClass();
	}

	@Test
	public void testGetTestCaseParentModulo() {
		System.setProperty("baseClass_br.gov.serpro.sys.view", "TestView");

		javaClass.setPackageName("br.gov.serpro.sys.view.modulo");
		assertEquals("TestView", javaClass.getTestCaseParent());

		javaClass.setPackageName("br.gov.serpro.sys.view");
		assertEquals("TestView", javaClass.getTestCaseParent());

		javaClass.setPackageName("br.gov.serpro.sys.busines");
		assertEquals(null, javaClass.getTestCaseParent());
	}

}
