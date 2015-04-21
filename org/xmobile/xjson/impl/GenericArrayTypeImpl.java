package org.xmobile.xjson.impl;

import java.io.Serializable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

import org.xmobile.xjson.XJsonType;

public class GenericArrayTypeImpl extends XJsonTypes implements GenericArrayType, Serializable {
	private static final long serialVersionUID = 0;
	private final Type componentType;

	public GenericArrayTypeImpl(Type componentType) {
		this.componentType = XJsonType.canonicalize(componentType);
	}

	public Type getGenericComponentType() {
		return componentType;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof GenericArrayType && equals(this, (GenericArrayType) o);
	}

	@Override
	public int hashCode() {
		return componentType.hashCode();
	}

	@Override
	public String toString() {
		return typeToString(componentType) + "[]";
	}
}
