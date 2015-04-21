package org.xmobile.xjson;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class XJSonString {
	
	private Object obj = null;
	public XJSonString(){
	}
	
	public XJSonString(Object o){
		setObject(o);
	}
	
	public void setObject(Object o){
		obj = getObjects(o);
	}
	
	@Override
	public String toString() {
		String jsonStr = null;
		if(obj instanceof JSONObject || obj instanceof JSONArray){
			jsonStr = obj.toString();
		}else{
			jsonStr = JSONObject.quote(obj.toString());
		}
		return jsonStr;
	}

	/******************************************************************
	 * Judge the object's kinds
	 ******************************************************************/
	private Object getObjects(Object o){
		Object obj = null;
		if(XUtils.isMap(o)){
			obj = doMap(o);
		}else if(XUtils.isList(o)){
			obj = doArray(((List<?>) o).toArray());
		}else if(XUtils.isArray(o)){
			obj = doArray((Object[]) o);
		}else if(!XUtils.isJavaClass(o)){
			obj = doObject(o);
		}else{
			/*******************
			 * 1. Primitive
			 * 2. Java Class
			 *******************/
			obj = o;
		}
		return obj;
	}
	
	/******************************************************************
	 * Object Operation
	 ******************************************************************/
	private JSONObject doObject(Object o){
		JSONObject jo = new JSONObject();
		toJsonObject(jo, o, o.getClass());
		return jo;
	}
	
	/******************************************************************
	 * Map Operation
	 ******************************************************************/
	private JSONObject doMap(Object o){
		JSONObject jo = new JSONObject();
		toJsonMap(jo, (Map<?, ?>)o);
		return jo;
	}
	
	/******************************************************************
	 * Array Operation
	 * 1. Object[]
	 * 2. List.toArray
	 ******************************************************************/
	private JSONArray doArray(Object[] o){
		JSONArray ja = new JSONArray();
		toJsonArray(ja, o);
		return ja;
	}

	/******************************************************************
	 * Add to JSONObject
	 ******************************************************************/
	private void addJsonObject(JSONObject jo, Object o, String keyName){
		Object object = getObjects(o);
		try {
			jo.put(keyName, object);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/******************************************************************
	 * Convert to JSONObject
	 * 
	 * Reflect the class's field
	 ******************************************************************/
	private void toJsonObject(JSONObject jo, Object o, Class<?> clz){
		Field[] fields = clz.getDeclaredFields();
		for(Field field : fields){
			if(field.getName().equals("serialVersionUID")){
				continue;
			}

			field.setAccessible(true);
			Method method = XJsonReflect.getGetDeclareMethod(clz, field);
			try {
				Object obj = method.invoke(o);
				if(obj == null){
					continue;
				}
				addJsonObject(jo, obj, field.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(clz.getGenericSuperclass() != null){
			toJsonObject(jo, o, clz.getSuperclass());
		}
	}

	/******************************************************************
	 * Convert to JSONArray (List or Array)
	 ******************************************************************/
	private <T> void toJsonArray(JSONArray jarray, Object[] list){
		if(list == null || list.length == 0){
			return;
		}

		for(Object o : list){
			jarray.put(getObjects(o));
		}
	}

	/******************************************************************
	 * Convert to JSONObject (Map)
	 ******************************************************************/
	private void toJsonMap(JSONObject jo, Map<?, ?> object){
		if(object == null || object.size() == 0){
			return;
		}
		
		Iterator<?> iterator = object.keySet().iterator();
		while(iterator.hasNext()) {
			Object key = iterator.next();
			Object o = object.get(key);
			addJsonObject(jo, o, key.toString());
		}
	}
}
