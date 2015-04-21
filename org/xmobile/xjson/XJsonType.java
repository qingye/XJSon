package org.xmobile.xjson;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

import org.xmobile.xjson.impl.GenericArrayTypeImpl;
import org.xmobile.xjson.impl.ParameterizedTypeImpl;
import org.xmobile.xjson.impl.WildcardTypeImpl;
import org.xmobile.xjson.impl.XJsonTypes;

public class XJsonType<T> {

	private Class<? super T> rawType = null;
	private Type type = null;
	private int hashCode = 0;

	@SuppressWarnings("unchecked")
	protected XJsonType() {
		this.type = getSuperclassTypeParameter(getClass());
		this.rawType = (Class<? super T>) new XJsonTypes().getRawType(this.type);
		this.setHashCode(this.type.hashCode());
	}

	@SuppressWarnings("unchecked")
	protected XJsonType(Type type) {
		this.type = canonicalize(XUtils.checkNotNull(type));
		this.rawType = (Class<? super T>) new XJsonTypes().getRawType(this.type);
		this.setHashCode(this.type.hashCode());
	}

	@SuppressWarnings("rawtypes")
	public static XJsonType<?> get(Type type){
		return new XJsonType(type);
	}
	
	@SuppressWarnings("rawtypes")
	public static XJsonType<?> get(Class<?> type) {
		return new XJsonType(type);
	}
	
	public final Class<? super T> getRawType() {
		return rawType;
	}

	public final Type getType() {
		return type;
	}

	private Type getSuperclassTypeParameter(Class<?> subclass) {
		Type superclass = subclass.getGenericSuperclass();
		if ((superclass instanceof Class)) {
			throw new RuntimeException("Missing type parameter.");
		}
		ParameterizedType parameterized = (ParameterizedType) superclass;
		return canonicalize(parameterized.getActualTypeArguments()[0]);
	}

	public static Type canonicalize(Type type) {
		if (type instanceof Class) {
			Class<?> c = (Class<?>) type;
			return c.isArray() ? new GenericArrayTypeImpl(canonicalize(c.getComponentType())) : c;

		} else if (type instanceof ParameterizedType) {
			ParameterizedType p = (ParameterizedType) type;
			return new ParameterizedTypeImpl(p.getOwnerType(), p.getRawType(),p.getActualTypeArguments());

		} else if (type instanceof GenericArrayType) {
			GenericArrayType g = (GenericArrayType) type;
			return new GenericArrayTypeImpl(g.getGenericComponentType());

		} else if (type instanceof WildcardType) {
			WildcardType w = (WildcardType) type;
			return new WildcardTypeImpl(w.getUpperBounds(), w.getLowerBounds());

		} else {
			return type;
		}
	}

	public int getHashCode() {
		return hashCode;
	}
	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}
}
