package br.gov.serpro.tools.junit;

import java.util.HashMap;
import java.util.Map;

import br.gov.serpro.tools.junit.model.Type;

/**
 * Generate unique values for same Type. 
 */
public class NextValueForType {
	private Map<Type, Integer> ids = new HashMap<Type, Integer>();
	
	private int nextId(Type type) {
		Integer id = ids.get(type);
		if (id == null) {
			id = 1;
		} else {
			id++;
		}
		ids.put(type, id);
		return id;
	}
	
	public String next(Type type) {
		final String name = type.getName();
		if (!type.isPrimitive()) {
			if (type.isCollection()) {
				return collectionInstance(type);
			} else if (name.equals("Boolean")) {
				return "Boolean.FALSE";
			} else if (name.equals("String")) {
			    return String.format("\"%d\"", nextId(type));
			}
			return String.format("new %s(%d)", name, nextId(type));
		}

		if (name.equals("boolean")) {
			return "false";
		}

		return "0";
	}

	private String collectionInstance(Type type) {
		Type[] generic = type.getGeneric();
		if (generic == null) {
			return String.format("new %s()", type.getDefaultCollectionImpl());
		} else {
			if (type.isSet()) {
				return String.format("newHashSet(%s)", next(generic[0]));
			} else if (type.isMap()) {
				return String.format("newHashMap(%s, %s)", next(generic[0]), next(generic[1]));
			} else if (type.isList()) {
				return String.format("Arrays.asList(%s)", next(generic[0]));
			} else {
				return type.getName();
			}
		}
	}

}
