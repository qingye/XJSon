package org.xmobile.xjson;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

public class XUtils {

	public static String capitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuilder(strLen)
            .append(Character.toTitleCase(str.charAt(0)))
            .append(str.substring(1))
            .toString();
    }

	/***********************************************************
	 * Class
	 ***********************************************************/
	public static boolean isJavaClass(Class<?> clz){
		if(Boolean.class.isAssignableFrom(clz) ||
		   Byte.class.isAssignableFrom(clz) ||
		   Character.class.isAssignableFrom(clz) ||
		   Double.class.isAssignableFrom(clz) ||
		   Float.class.isAssignableFrom(clz) ||
		   Integer.class.isAssignableFrom(clz) ||
		   Long.class.isAssignableFrom(clz) ||
		   Short.class.isAssignableFrom(clz) ||
		   String.class.isAssignableFrom(clz)){
			return true;
		}
		
		if(isPrimitive(clz)){
			return true;
		}
		
		if(clz.getPackage().getName().startsWith("java.")){
			return true;
		}
		return false;
	}
	
	public static boolean isJavaClass(Object o){
		return isJavaClass(o.getClass());
	}
	
	/***********************************************************
	 * Primitive
	 ***********************************************************/
	public static boolean isPrimitive(Class<?> clz){
		return clz.isPrimitive();
	}

	/***********************************************************
	 * TypeVariable
	 ***********************************************************/
	public static boolean isTypeVariable(Type type){
		return ((type != null) && (type instanceof TypeVariable));
	}
	
	/***********************************************************
	 * ParameterizedType
	 ***********************************************************/
	public static boolean isParameterizedType(Type type){
		return ((type != null) && (type instanceof ParameterizedType));
	}
	
	/***********************************************************
	 * GenericArrayType
	 ***********************************************************/
	public static boolean isGenericArrayType(Type type){
		return ((type != null) && (type instanceof GenericArrayType));
	}
	
	/***********************************************************
	 * List
	 ***********************************************************/
	public static boolean isList(Object o){
		return o instanceof Collection;
	}
	public static boolean isList(Class<?> clz){
		return Collection.class.isAssignableFrom(clz);
	}
	public static boolean isArrayList(Class<?> clz){
		return ArrayList.class.isAssignableFrom(clz);
	}
	public static boolean isArrayList(Object o){
		return o instanceof ArrayList;
	}
	public static boolean isLinkedList(Class<?> clz){
		return LinkedList.class.isAssignableFrom(clz);
	}
	public static boolean isLinkedList(Object o){
		return o instanceof LinkedList;
	}
	public static boolean isVector(Class<?> clz){
		return Vector.class.isAssignableFrom(clz);
	}
	public static boolean isVector(Object o){
		return o instanceof Vector;
	}

	/***********************************************************
	 * Array
	 ***********************************************************/
	public static boolean isArray(Object o){
		return o.getClass().isArray();
	}
	public static boolean isArray(Class<?> clz){
		return clz.isArray();
	}
	
	/***********************************************************
	 * Map
	 ***********************************************************/
	public static boolean isMap(Object o){
		return o instanceof Map;
	}
	public static boolean isMap(Class<?> clz){
		return Map.class.isAssignableFrom(clz);
	}
	
	public static <T> T checkNotNull(T obj) {
		if (obj == null) {
			throw new NullPointerException();
		}
		return obj;
	}

	public static void checkArgument(boolean condition) {
		if (!condition) {
			throw new IllegalArgumentException();
		}
	}
	
	public static void checkNotPrimitive(Type type) {
	    checkArgument(!(type instanceof Class<?>) || !isPrimitive((Class<?>) type));
	  }
}
