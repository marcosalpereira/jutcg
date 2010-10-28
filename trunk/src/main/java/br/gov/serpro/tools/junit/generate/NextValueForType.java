package br.gov.serpro.tools.junit.generate;

import java.util.HashMap;
import java.util.Map;

import br.gov.serpro.tools.junit.model.Type;
import br.gov.serpro.tools.junit.util.Config;

/**
 * Generate unique values for same Type.
 */
public class NextValueForType {

    /**
     * Value that can be used at value template for a type.
     */
    private static final String VALUE_TEMPLATE_ID_VAR = "@id";

    /**
     * Map to control sequential values for each type.
     */
    private final Map<Type, Integer> ids = new HashMap<Type, Integer>();

    private int nextId(final Type type) {
        Integer id = this.ids.get(type);
        if (id == null) {
            id = 1;
        } else {
            id++;
        }
        this.ids.put(type, id);
        return id;
    }

    public String next(final Type type) {
        final String name = type.getName();
        if (!type.isPrimitive()) {
            if (type.isCollection()) {
                return collectionInstance(type);
            } else if (name.equals("Boolean")) {
                return "Boolean.FALSE";
            } else if (name.equals("String")) {
                return String.format("\"%d\"", nextId(type));
            }
            // Check for a value template for this type
            final String valueTemplate = Config.getString(String.format("valueTemplateFor.%s",
                    name));
            if (valueTemplate != null) {
                if (valueTemplate.contains(VALUE_TEMPLATE_ID_VAR)) {
                    return valueTemplate.replaceAll(VALUE_TEMPLATE_ID_VAR, String
                            .valueOf(nextId(type)));
                } else {
                    return valueTemplate;
                }
            } else {
                return String.format("new %s(%d)", name, nextId(type));
            }
        }

        if (name.equals("boolean")) {
            return "false";
        }

        return "0";
    }

    private String collectionInstance(final Type type) {
        final Type[] generic = type.getGeneric();
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
