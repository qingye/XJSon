package org.xmobile.xjson.impl;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

import org.xmobile.xjson.XUtils;

public class XJsonTypes {

	protected boolean equal(Object a, Object b) {
		return a == b || (a != null && a.equals(b));
	}

	protected boolean equals(Type a, Type b) {
		if (a == b) {
			// also handles (a == null && b == null)
			return true;

		} else if (a instanceof Class) {
			// Class already specifies equals().
			return a.equals(b);

		} else if (a instanceof ParameterizedType) {
			if (!(b instanceof ParameterizedType)) {
				return false;
			}

			// TODO: save a .clone() call
			ParameterizedType pa = (ParameterizedType) a;
			ParameterizedType pb = (ParameterizedType) b;
			return equal(pa.getOwnerType(), pb.getOwnerType())
					&& pa.getRawType().equals(pb.getRawType())
					&& Arrays.equals(pa.getActualTypeArguments(),
							pb.getActualTypeArguments());

		} else if (a instanceof GenericArrayType) {
			if (!(b instanceof GenericArrayType)) {
				return false;
			}

			GenericArrayType ga = (GenericArrayType) a;
			GenericArrayType gb = (GenericArrayType) b;
			return equals(ga.getGenericComponentType(),
					gb.getGenericComponentType());

		} else if (a instanceof WildcardType) {
			if (!(b instanceof WildcardType)) {
				return false;
			}

			WildcardType wa = (WildcardType) a;
			WildcardType wb = (WildcardType) b;
			return Arrays.equals(wa.getUpperBounds(), wb.getUpperBounds())
					&& Arrays.equals(wa.getLowerBounds(), wb.getLowerBounds());

		} else if (a instanceof TypeVariable) {
			if (!(b instanceof TypeVariable)) {
				return false;
			}
			TypeVariable<?> va = (TypeVariable<?>) a;
			TypeVariable<?> vb = (TypeVariable<?>) b;
			return va.getGenericDeclaration() == vb.getGenericDeclaration()
					&& va.getName().equals(vb.getName());

		} else {
			// This isn't a type we support. Could be a generic array type,
			// wildcard type, etc.
			return false;
		}
	}

	public Class<?> getRawType(Type type) {
		if (type instanceof Class<?>) {
			// type is a normal class.
			return (Class<?>) type;

		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;

			// I'm not exactly sure why getRawType() returns Type instead of
			// Class.
			// Neal isn't either but suspects some pathological case related
			// to nested classes exists.
			Type rawType = parameterizedType.getRawType();
			XUtils.checkArgument(rawType instanceof Class);
			return (Class<?>) rawType;

		} else if (type instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) type)
					.getGenericComponentType();
			return Array.newInstance(getRawType(componentType), 0).getClass();

		} else if (type instanceof TypeVariable) {
			// we could use the variable's bounds, but that won't work if there
			// are multiple.
			// having a raw type that's more general than necessary is okay
			return Object.class;

		} else if (type instanceof WildcardType) {
			return getRawType(((WildcardType) type).getUpperBounds()[0]);

		} else {
			String className = type == null ? "null" : type.getClass()
					.getName();
			throw new IllegalArgumentException(
					"Expected a Class, ParameterizedType, or "
							+ "GenericArrayType, but <" + type
							+ "> is of type " + className);
		}
	}

	protected String typeToString(Type type) {
		return type instanceof Class ? ((Class<?>) type).getName() : type
				.toString();
	}

	protected int hashCodeOrZero(Object o) {
		return o != null ? o.hashCode() : 0;
	}
}
