package org.xmobile.xjson;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class XJsonReflect {

	/******************************************************************
	 * Get method by field
	 ******************************************************************/
	public static Method getSetDeclareMethod(Class<?> clz, Field field){
		Class<?> fieldType = getFieldType(clz, field.getName());
		String md = "set" + XUtils.capitalize(field.getName());
		return getDeclareMethod(clz, md, fieldType);
	}
	
	public static Method getGetDeclareMethod(Class<?> clz, Field field){
		String[] md = {"get", "is"};
		String funcName = XUtils.capitalize(field.getName());
		Method method = null;
		
		for(int i = 0; i < md.length; i ++){
			method = getDeclareMethod(clz, md[i] + funcName, null);
			if(method != null){
				break;
			}
		}
		return method;
	}
	
	/******************************************************************
	 * Field Type
	 ******************************************************************/
	public static Class<?> getFieldType(Class<?> c, String fieldName){
		Class<?> type = null;
		
		Field[] fields = c.getDeclaredFields();
		for(int i = 0; i < fields.length; i ++){
			if(fields[i].getName().equals(fieldName)){
				type = fields[i].getType();
				break;
			}
		}
		
		if(type == null && c.getGenericSuperclass() != null){
			type = getFieldType(c.getSuperclass(), fieldName);
		}
		
		return type;
	}
	
	/******************************************************************
	 * Get method by field & field's type
	 ******************************************************************/
	private static Method getDeclareMethod(Class<?> clz, String method, Class<?> type){
		Method m = null;
		try {
			if(type != null){
				m = clz.getDeclaredMethod(method, type);
			}else{
				m = clz.getDeclaredMethod(method);
			}
		} catch (NoSuchMethodException e) {
		}
		
		if(m == null && clz.getGenericSuperclass() != null){
			m = getDeclareMethod(clz.getSuperclass(), method, type);
		}
		return m;
	}
}
