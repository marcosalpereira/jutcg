package br.gov.serpro.tools.junit.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.js.Parameter;
import org.apache.ws.jaxme.js.util.JavaParser;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import br.gov.serpro.tools.junit.model.Field;
import br.gov.serpro.tools.junit.model.FieldMethodInvocation;
import br.gov.serpro.tools.junit.model.Flow;
import br.gov.serpro.tools.junit.model.FormalParameter;
import br.gov.serpro.tools.junit.model.JavaClass;
import br.gov.serpro.tools.junit.model.Method;
import br.gov.serpro.tools.junit.model.Protection;
import br.gov.serpro.tools.junit.model.Type;

public class JavaSourceParser implements SourceParser {

	private JavaSource javaSource;
	private JavaClass javaClass;

	/** {@inheritDoc} */
	@Override
	public JavaClass getSource() {
		javaClass = new JavaClass();
		javaClass.setType(translateType(javaSource.getQName()));
		javaClass.setImports(translateImports(javaSource.getImports()));
		javaClass.setFields(translateFields(javaSource.getFields()));
		javaClass.setMethods(translateMethods(javaSource.getMethods()));
		javaClass.setPackageName(javaSource.getPackageName());
		return javaClass;
	}

	private Type translateType(JavaQName javaQName) {
		Type type = new Type();
		type.setName(javaQName.getClassName());
		type.setFullName(javaQName.toString());
		type.setPrimitive(isPrimitive(type.getName()));
		return type;
	}

	private boolean isPrimitive(String type) {
		return "byte,short,int,long,float,double,boolean".indexOf(type) != -1;
	}

	private List<Method> translateMethods(JavaMethod[] methods) {
		List<Method> ret = new ArrayList<Method>(methods.length);
		for (JavaMethod javaMethod : methods) {
			Method method = new Method();
			method.setName(javaMethod.getName());
			method.setProtection(translateProtection(javaMethod.getProtection()));
			method.setType(translateType(javaMethod.getType()));
			method.setJavaClass(javaClass);
			method.setFormalParameters(translateParameters(javaMethod.getParams()));

			//TODO remover
			tmpCriarFluxos(method);
			
			ret.add(method);
		}
		return ret;
	}

	private void tmpCriarFluxos(Method method) {
		if (javaClass.getType().getName().equals("ValidarNotasBean")
				&& method.getName().equals("validarNotas")) {
			method.getFlows().add(tmpCriarFluxo_ValidarNotasBean_ValidarNotas(method));
		}
		if (javaClass.getType().getName().equals("ResultadoProvaManualBusinessBean")
				&& method.getName().equals("consultarProvasComResultadoCadastrado")) {
			method.getFlows().add(tmpCriarFluxo_ResultadoProvaManualBusinessBean_consultarProvasComResultadoCadastrado(method));
		}
	}

	private Flow tmpCriarFluxo_ResultadoProvaManualBusinessBean_consultarProvasComResultadoCadastrado(Method method) {
		Flow f1 = new Flow();
		f1.setName("Fluxo1");
		f1.setMethod(method);
		
		List<FieldMethodInvocation> invocations = f1.getInvocations();
		FieldMethodInvocation inv1 = new FieldMethodInvocation();
		inv1.setInvokedAtField(javaClass.searchField("resultadoProvaManualDao"));
		inv1.setMethod(tmpCriarMetodo_selecionarProvasComResultadoCadastrado());
		inv1.getArguments().add("concurso");
		inv1.setReturnInvocation(true);
		invocations.add(inv1);
		
		return f1;
	}
	private Method tmpCriarMetodo_selecionarProvasComResultadoCadastrado() {
		Method met = new Method();
		met.setName("selecionarProvasComResultadoCadastrado");
		Type type = new Type();
		type.setName("List<Prova>");
		type.setPrimitive(false);
		met.setType(type);
		return met;
	}
	
	private Flow tmpCriarFluxo_ValidarNotasBean_ValidarNotas(Method method) {
		Flow f1 = new Flow();
		f1.setName("Fluxo1");
		f1.setMethod(method);

		List<FieldMethodInvocation> invocations = f1.getInvocations();
		FieldMethodInvocation inv1 = new FieldMethodInvocation();
		inv1.setInvokedAtField(javaClass.searchField("facade"));
		inv1.setMethod(tmpCriarMetodo_todasAsNotasForamPreenchidas());
		inv1.getArguments().add("provaSelecionada");
		inv1.getArguments().add("versaoSelecionada");
		inv1.setReturnInvocation(false);
		invocations.add(inv1);

		FieldMethodInvocation inv2 = new FieldMethodInvocation();
		inv2.setInvokedAtField(javaClass.searchField("facade"));
		inv2.setMethod(tmpCriarMetodo_validarNotas());
		inv2.getArguments().add("provaSelecionada");
		inv2.getArguments().add("versaoSelecionada");
		inv2.setReturnInvocation(false);
		invocations.add(inv2);		
		
		f1.getReadFields().add(javaClass.searchField("provaSelecionada"));
		f1.getReadFields().add(javaClass.searchField("versaoSelecionada"));
		f1.getWrittenFields().add(javaClass.searchField("mostrarBotaoValidacao"));
		f1.getWrittenFields().add(javaClass.searchField("mostrarBotaoCancelarValidacao"));
		return f1;
	}

	private Method tmpCriarMetodo_todasAsNotasForamPreenchidas() {
		Method met = new Method();
		met.setName("todasAsNotasForamPreenchidas");
		Type type = new Type();
		type.setName("boolean");
		type.setPrimitive(true);
		met.setType(type);
		return met;
	}
	
	private Method tmpCriarMetodo_validarNotas() {
		Method met = new Method();
		met.setName("validarNotas");
		return met;
	}

	private List<FormalParameter> translateParameters(Parameter[] params) {
		List<FormalParameter> ret = new ArrayList<FormalParameter>(params.length);
		for (Parameter parameter : params) {
			FormalParameter fp = new FormalParameter();
			fp.setVariableId(parameter.getName());
			fp.setType(translateType(parameter.getType()));
			ret.add(fp);
		}
		return ret;
	}

	private Protection translateProtection(
			org.apache.ws.jaxme.js.JavaSource.Protection protection) {
		String sProtection = protection.toString();
		if ("public".equals(sProtection)) {
			return Protection.PUBLIC;
		} else if ("protected".equals(sProtection)) {
			return Protection.PROTECTED;
		} else if ("private".equals(sProtection)) {
			return Protection.PRIVATE;
		} else if (sProtection == null  ||  "".equals(sProtection)) {
			return Protection.DEFAULT;
		} else {
			throw new IllegalArgumentException(
					"Protection must be either 'public', 'protected', 'private', null or '' (default protection).");
		}
	}

	private List<Field> translateFields(JavaField[] fields) {
		List<Field> ret = new ArrayList<Field>(fields.length);
		for (JavaField javaField : fields) {
			Field field = new Field();
			field.setName(javaField.getName());
			field.setProtection(translateProtection(javaField.getProtection()));
			field.setStatic(javaField.isStatic());
			field.setType(translateType(javaField.getType()));
			//TODO del
			if ("resultadoProvaManualDao;facade;paginacaoBean;relatorioService".indexOf(field.getName()) != -1) {
				field.getAnnotations().add("EJB");
			}

			ret.add(field);
		}
		return ret;
	}

	private List<String> translateImports(JavaQName[] imports) {
		List<String> ret = new ArrayList<String>(imports.length);
		for (JavaQName javaQName : imports) {
			ret.add(javaQName.toString());
		}
		return ret;
	}

	/** {@inheritDoc} */
	@Override
	public void parse(File file) throws ParseException {
		 try {
			javaSource = parseSourceFile(file);
		} catch (RecognitionException e) {
			throw new ParseException(e);
		} catch (TokenStreamException e) {
			throw new ParseException(e);
		} catch (FileNotFoundException e) {
			throw new ParseException(e);
		}
	}

	public JavaSource parseSourceFile(File file) throws RecognitionException,
			TokenStreamException, FileNotFoundException {
		JavaSourceFactory jsf = new JavaSourceFactory();
		JavaParser jp = new JavaParser(jsf);
		jp.parse(file);
		JavaSource js = (JavaSource) jsf.getJavaSources().next();
		return js;
	}

}
