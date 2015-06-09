package org.xmobile.xjson;

import java.lang.reflect.Type;


public class XJSon {

	public String toJson(Object o){
		XJSonString xString = new XJSonString(o);
		return xString.toString();
	}
	
	public Object fromJson(String json, Type type){
		XJsonObject<?> xObject = new XJsonObject<Object>(json, type);
		return xObject.getObj();
	}
}
