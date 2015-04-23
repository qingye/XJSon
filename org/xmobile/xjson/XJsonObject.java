package org.xmobile.xjson;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

public class XJsonObject<T> {

	private Object obj = null;
	public XJsonObject(){
	}
	
	public XJsonObject(String json, Type type){
		setJson(json, type);
	}
	
	public void setJson(String json, Type type){
		obj = doInit(jsonToObjects(json, XJsonType.get(type).getRawType()), type);
	}

	public Object getObj() {
		return obj;
	}

	/******************************************************************
	 * Judge the object's kinds
	 ******************************************************************/
	private Object jsonToObjects(String json, Class<?> clz){
		Object obj = null;
		if(XUtils.isList(clz) || XUtils.isArray(clz)){
			obj = initArray(json);
		}else if(XUtils.isMap(clz) || !XUtils.isJavaClass(clz)){
			obj = initObject(json);
		}else{
			obj = json;
		}
		return obj;
	}
	
	/******************************************************************
	 * output: JSONObject
	 ******************************************************************/
	private JSONObject initObject(String json){
		JSONObject jo = null;
		try {
			jo = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jo;
	}
	
	/******************************************************************
	 * output: JSONArray
	 ******************************************************************/
	private JSONArray initArray(String json){
		JSONArray ja = null;
		try {
			ja = new JSONArray(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ja;
	}
	
	/******************************************************************
	 * Reflect to instance
	 ******************************************************************/
	private Object doInit(Object o, Type type) {
		Class<?> clz = XJsonType.get(type).getRawType();
		Object obj = null;
		if(o instanceof JSONObject){
			if(XUtils.isMap(clz)){
				obj = doMap(o, type);
			}else{
				obj = doObject(o, type);
			}
		}else if(o instanceof JSONArray){
			obj = doArray(o, type);
		}else{
			obj = o;
		}
		return obj;
	}

	/******************************************************************
	 * Create instance: Object / List
	 ******************************************************************/
	private Object getObjectByType(Type type){
		Class<?> clz = XJsonType.get(type).getRawType();
		Object o = null;
		
		if(XUtils.isList(clz) || XUtils.isArray(clz)){
			if(XUtils.isLinkedList(clz)){
				o = new LinkedList<Object>();
			}else if(XUtils.isVector(clz)){
				o = new Vector<Object>();
			}else{
				o = new ArrayList<Object>();
			}
		}else if(XUtils.isMap(clz)){
			o = new HashMap<Object, Object>();
		}else if(!XUtils.isJavaClass(clz)){
			try {
				o = clz.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return o;
	}

	/******************************************************************
	 * If the type isn't basic, then recursive
	 ******************************************************************/
	private Type[] getActualType(Type type){
		Type[] t = null;
		if(type instanceof Class<?> && !((Class<?>)type).isArray()){
			t = new Type[]{type};
		}else if(XUtils.isParameterizedType(type)){
			t = ((ParameterizedType)type).getActualTypeArguments();
		}else if(XUtils.isGenericArrayType(type)){
			t = new Type[]{((GenericArrayType)type).getGenericComponentType()};
		}else{
			t = getActualType(XJsonType.get(type).getType());
		}
		return t;
	}

	/******************************************************************
	 * Objects
	 ******************************************************************/
	private Object getObjects(Object o, Type type){
		Class<?> clz = XJsonType.get(type).getRawType();
		Object obj = null;
		if(XUtils.isList(clz) || XUtils.isArray(clz)){
			obj = doArray(o, type);
		}else if(XUtils.isMap(clz)){
			obj = doMap(o, type);
		}else if(!XUtils.isJavaClass(clz)){
			obj = doObject(o, type);
		}else{
			obj = o;
		}
		return obj;
	}
	
	/******************************************************************
	 * Auto convert into the type of destination
	 ******************************************************************/
	private Object adjustObjectByType(Object obj, Class<?> clz, Field field){
		if(obj == null){
			return obj;
		}
		
		Class<?> fieldType = XJsonReflect.getFieldType(clz, field.getName());
		if(!obj.getClass().equals(fieldType)){
			if(fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)){
				obj = toBoolean(obj);
			}else if(fieldType.equals(Integer.class) || fieldType.equals(int.class)){
				obj = toInteger(obj);
			}else if(fieldType.equals(Double.class) || fieldType.equals(double.class)){
				obj = toDouble(obj);
			}else if(fieldType.equals(Float.class) || fieldType.equals(float.class)){
				obj = toFloat(obj);
			}else if(fieldType.equals(String.class)){
				obj = toStrings(obj);
			}
		}
		return obj;
	}

	private Object toBoolean(Object obj){
		if(obj.toString().equalsIgnoreCase("true") || obj.toString().equalsIgnoreCase("1")){
			obj = true;
		}else{
			obj = false;
		}
		return obj;
	}
	
	private Object toInteger(Object obj){
		if(obj.toString().equalsIgnoreCase("true")){
			obj = 1;
		}else if(obj.toString().equalsIgnoreCase("false")){
			obj = 0;
		}
		return ((Double)toDouble(obj)).intValue();
	}
	
	private Object toDouble(Object obj){
		return Double.parseDouble(String.valueOf(obj));
	}
	
	private Object toFloat(Object obj){
		return Float.parseFloat(String.valueOf(obj));
	}
	
	private Object toStrings(Object obj){
		return String.valueOf(obj);
	}

	/******************************************************************
	 * Get object by key (K,V)
	 ******************************************************************/
	private Object getValueByKey(JSONObject jo, String key){
		Object obj = null;
		try {
			obj = jo.get(key);
			if(obj != null && obj.toString().equalsIgnoreCase("null")){
				obj = null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	/******************************************************************
	 * Go through the fields of the class
	 ******************************************************************/
	private void toObject(JSONObject jo, Object o, Type type){
		Class<?> clz = XJsonType.get(type).getRawType();
		Field[] fields = clz.getDeclaredFields();
		for(Field field : fields){
			if(field.getName().equals("serialVersionUID")){
				continue;
			}

			field.setAccessible(true);
			Method method = XJsonReflect.getSetDeclareMethod(clz, field);
			Object obj = getValueByKey(jo, field.getName());
			if(obj == null){
				continue;
			}
			obj = getObjects(obj, getGenericType(o, field, type));
			obj = adjustObjectByType(obj, clz, field);
			
			try {
				if(method != null){
					method.invoke(o, getInvokeParams(obj, field.getGenericType()));
				}
			} catch (Exception e) {
				System.out.println("[toObject] field.Name = " + field.getName() + "," + e.toString());
			}
		}
		
		if(clz.getGenericSuperclass() != null){
			toObject(jo, o, clz.getSuperclass());
		}
	}
	
	/******************************************************************
	 * Check TypeVariable
	 ******************************************************************/
	private Type getGenericType(Object o, Field field, Type type){
		Type genericType = field.getGenericType();
		if(XUtils.isTypeVariable(genericType)){
			if(type instanceof ParameterizedType){
				genericType = ((ParameterizedType) type).getActualTypeArguments()[0];
			}else{
				ParameterizedType genType = (ParameterizedType) o.getClass().getGenericSuperclass();
				Class<?> rawType = (Class<?>) genType.getRawType();

				int typeIndex = 0;
				Type[] typeParams = rawType.getTypeParameters();
				if(typeParams != null){
			        for(int i = 0; i < typeParams.length; i ++){
			        	if(genericType.equals(typeParams[i])){
			        		typeIndex = i;
			        		break;
			        	}
			        }
		        }
				
		        Type[] params = genType.getActualTypeArguments();
		        if(params != null && typeIndex < params.length){
		        	genericType = params[typeIndex];
		        }
			}
		}
		
		return genericType;
	}
	
	/******************************************************************
	 * Encapsulation the invoke parameters
	 ******************************************************************/
	@SuppressWarnings("unchecked")
	private Object[] getInvokeParams(Object obj, Type type){
		Object[] o = new Object[1];
		Class<?> clz = XJsonType.get(type).getRawType();
		if(XUtils.isArray(clz)){
			Type actType = getActualType(type)[0];
			T[] t = (T[]) Array.newInstance((Class<?>) XJsonType.get(actType).getRawType(), ((T[])obj).length);
			for(int i = 0; i < ((T[])obj).length; i ++){
				t[i] = (T)((T[])obj)[i];
			}
			o[0] = t;
		}else{
			o[0] = obj;
		}
		return o;
	}
	
	/******************************************************************
	 * JSONObject => Object
	 ******************************************************************/
	private Object doObject(Object jo, Type type){
		Object o = getObjectByType(type);
		if(o != null){
			toObject((JSONObject)jo, o, type);
		}
		return o;
	}

	/******************************************************************
	 * JSONArray => List/Array
	 ******************************************************************/
	@SuppressWarnings("unchecked")
	private Object doArray(Object jo, Type type){
		Object o = null;
		Type t = getActualType(type)[0];
		Class<?> rawClazz = XJsonType.get(type).getRawType();

		JSONArray ja = (JSONArray) jo;
		if(ja != null && ja.length() > 0){
			ArrayList<Object> list = (ArrayList<Object>) getObjectByType(type);
			for(int i = 0; i < ja.length(); i ++){
				Object object = null;
				try {
					object = ja.get(i);
					if(object != null){
						object = getObjects(object, t);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				if(object != null){
					list.add(object);
				}
			}

			if(XUtils.isList(rawClazz)){
				o = list;
			}else if(XUtils.isArray(rawClazz)){
				o = list.toArray();
			}
		}

		return o;
	}
	
	/******************************************************************
	 * JSONObject => Map
	 ******************************************************************/
	@SuppressWarnings("unchecked")
	private Object doMap(Object jo, Type type){
		Object o = getObjectByType(type);
		Type[] types = getActualType(type);

		JSONObject jobj = (JSONObject) jo;
		Iterator<?> iterator = jobj.keys();
		while(iterator.hasNext()) {
			Object key = iterator.next();
			Object v = getValueByKey(jobj, (String)key);
			if(v != null){
				v = getObjects(v, types[1]);
				((HashMap<Object, Object>)o).put(key, v);
			}
		}
		
		return o;
	}
}
