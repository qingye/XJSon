package org.xmobile.xjson.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

import org.xmobile.xjson.XJsonType;
import org.xmobile.xjson.XUtils;

public class ParameterizedTypeImpl extends XJsonTypes implements ParameterizedType, Serializable {
	private static final long serialVersionUID = 0;
	private final Type ownerType;
	private final Type rawType;
	private final Type[] typeArguments;

	public ParameterizedTypeImpl(Type ownerType, Type rawType,
			Type... typeArguments) {
		if (rawType instanceof Class<?>) {
			Class<?> rawTypeAsClass = (Class<?>) rawType;
			XUtils.checkArgument(ownerType != null || rawTypeAsClass.getEnclosingClass() == null);
			XUtils.checkArgument(ownerType == null|| rawTypeAsClass.getEnclosingClass() != null);
		}

		this.ownerType = ownerType == null ? null : XJsonType.canonicalize(ownerType);
		this.rawType = XJsonType.canonicalize(rawType);
		this.typeArguments = typeArguments.clone();
		for (int t = 0; t < this.typeArguments.length; t++) {
			XUtils.checkNotNull(this.typeArguments[t]);
			XUtils.checkNotPrimitive(this.typeArguments[t]);
			this.typeArguments[t] = XJsonType.canonicalize(this.typeArguments[t]);
		}
	}

	public Type[] getActualTypeArguments() {
		return typeArguments.clone();
	}

	public Type getRawType() {
		return rawType;
	}

	public Type getOwnerType() {
		return ownerType;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof ParameterizedType
				&& equals(this, (ParameterizedType) other);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(typeArguments) ^ rawType.hashCode() ^ hashCodeOrZero(ownerType);
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(30 * (typeArguments.length + 1));
		stringBuilder.append(typeToString(rawType));

		if (typeArguments.length == 0) {
			return stringBuilder.toString();
		}

		stringBuilder.append("<").append(typeToString(typeArguments[0]));
		for (int i = 1; i < typeArguments.length; i++) {
			stringBuilder.append(", ").append(typeToString(typeArguments[i]));
		}
		return stringBuilder.append(">").toString();
	}
}
