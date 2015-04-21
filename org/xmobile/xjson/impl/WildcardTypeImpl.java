package org.xmobile.xjson.impl;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

import org.xmobile.xjson.XJsonType;
import org.xmobile.xjson.XUtils;

public class WildcardTypeImpl extends XJsonTypes implements WildcardType,Serializable {
	private static final long serialVersionUID = 0;
	private final Type[] EMPTY_TYPE_ARRAY = new Type[] {};
	private final Type upperBound;
	private final Type lowerBound;

	public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
		XUtils.checkArgument(lowerBounds.length <= 1);
		XUtils.checkArgument(upperBounds.length == 1);

		if (lowerBounds.length == 1) {
			XUtils.checkNotNull(lowerBounds[0]);
			XUtils.checkNotPrimitive(lowerBounds[0]);
			XUtils.checkArgument(upperBounds[0] == Object.class);
			this.lowerBound = XJsonType.canonicalize(lowerBounds[0]);
			this.upperBound = Object.class;

		} else {
			XUtils.checkNotNull(upperBounds[0]);
			XUtils.checkNotPrimitive(upperBounds[0]);
			this.lowerBound = null;
			this.upperBound = XJsonType.canonicalize(upperBounds[0]);
		}
	}

	public Type[] getUpperBounds() {
		return new Type[] { upperBound };
	}

	public Type[] getLowerBounds() {
		return lowerBound != null ? new Type[] { lowerBound }
				: EMPTY_TYPE_ARRAY;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof WildcardType && equals(this, (WildcardType) other);
	}

	@Override
	public int hashCode() {
		return (lowerBound != null ? 31 + lowerBound.hashCode() : 1) ^ (31 + upperBound.hashCode());
	}

	@Override
	public String toString() {
		if (lowerBound != null) {
			return "? super " + typeToString(lowerBound);
		} else if (upperBound == Object.class) {
			return "?";
		} else {
			return "? extends " + typeToString(upperBound);
		}
	}
}