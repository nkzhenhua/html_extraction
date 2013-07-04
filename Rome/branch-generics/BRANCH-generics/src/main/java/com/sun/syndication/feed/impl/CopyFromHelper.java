/*
 * Copyright 2004 Sun Microsystems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.sun.syndication.feed.impl;

import com.sun.syndication.feed.CopyFrom;
import com.sun.syndication.feed.impl.BeanIntrospector;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author Alejandro Abdelnur
 */
public class CopyFromHelper {
    private static final Object[] NO_PARAMS = new Object[0];

    private Class<?> _beanInterfaceClass;
    private Map<String, Class<?>> _baseInterfaceMap; //ENTRIES(propertyName,interface.class)
    private Map<Class<?>, Class<?>> _baseImplMap;      //ENTRIES(interface.class,implementation.class)

    public CopyFromHelper(Class<?> beanInterfaceClass, Map<String, Class<?>> basePropInterfaceMap, Map<Class<?>, Class<?>> basePropClassImplMap) {
        _beanInterfaceClass = beanInterfaceClass;
        _baseInterfaceMap = basePropInterfaceMap;
        _baseImplMap = basePropClassImplMap;
    }

    public void copy(Object target,Object source) {
        try {
            PropertyDescriptor[] pds = BeanIntrospector.getPropertyDescriptors(_beanInterfaceClass);
            if (pds!=null) {
                for (PropertyDescriptor pd : pds) {
                    String propertyName = pd.getName();
                    Method pReadMethod = pd.getReadMethod();
                    Method pWriteMethod = pd.getWriteMethod();
                    if (pReadMethod!=null && pWriteMethod!=null &&       // ensure it has getter and setter methods
                        pReadMethod.getDeclaringClass()!=Object.class && // filter Object.class getter methods
                        pReadMethod.getParameterTypes().length==0 &&     // filter getter methods that take parameters
                        _baseInterfaceMap.containsKey(propertyName)) {   // only copies properties defined as copyFrom-able
                        Object value = pReadMethod.invoke(source,NO_PARAMS);
                        if (value!=null) {
                            Class<?> baseInterface = _baseInterfaceMap.get(propertyName);
                            value = doCopy(value,baseInterface);
                            pWriteMethod.invoke(target, new Object[]{value});
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("Could not do a copyFrom "+ex, ex);
        }
    }

    private CopyFrom<?> createInstance(Class<? extends CopyFrom<?>> interfaceClass) throws Exception {
    	CopyFrom<?> copy = null;
    	Class<?> implClass = _baseImplMap.get(interfaceClass);
    	if(implClass != null) {
    		copy = (CopyFrom<?>)implClass.newInstance();
    	}
    	return copy;
    }

    @SuppressWarnings("unchecked")
	private Object doCopy(Object value, Class<?> baseInterface) throws Exception {
        if (value!=null) {
            Class<?> vClass = value.getClass();
            if (vClass.isArray()) {
                value = doCopyArray(value,baseInterface);
            }
            else
            if (value instanceof Collection) {
                value = doCopyCollection((Collection<Object>)value, baseInterface);
            }
            else
            if (value instanceof Map) {
                value = doCopyMap((Map<Object, Object>)value,baseInterface);
            }
            else
            if (isBasicType(vClass)) {
                // value = value; // nothing to do here
                if (value instanceof Date) { // because Date it is not inmutable
                    value = ((Date)value).clone();
                }
            }
            else { // it goes CopyFrom
                if (value instanceof CopyFrom) {
                    CopyFrom<?> source = (CopyFrom<?>) value;
                    CopyFrom<?> target = createInstance(source.getInterface());
                    target = target == null ?  (CopyFrom<?>) value.getClass().newInstance() : target;
                    target.copyFrom(source);
                    value = target;
                }
                else {
                    throw new Exception("unsupported class for 'copyFrom' "+value.getClass());
                }
            }
        }
        return value;
    }

    private Object doCopyArray(Object array, Class<?> baseInterface) throws Exception {
        Class<?> elementClass = array.getClass().getComponentType();
        int length = Array.getLength(array);
        Object newArray = Array.newInstance(elementClass,length);
        for (int i=0;i<length;i++) {
            Object element = doCopy(Array.get(array,i),baseInterface);
            Array.set(newArray,i,element);
        }
        return newArray;
    }

    private Object doCopyCollection(Collection<Object> collection, Class<?> baseInterface) throws Exception {
    	Collection<Object> newCollection;
        // expecting SETs or LISTs only, going default implementation of them
    	if(collection instanceof Set) {
    		newCollection = new HashSet<Object>();
    	} else {
    		newCollection = new ArrayList<Object>();
    	}
        Iterator<Object> i = collection.iterator();
        while (i.hasNext()) {
            Object element = doCopy(i.next(), baseInterface);
            newCollection.add(element);
        }
        return newCollection;
    }

    private Object doCopyMap(Map<Object, Object> map, Class<?> baseInterface) throws Exception {
        Map<Object, Object> newMap = new HashMap<Object, Object>();
        Iterator<Entry<Object, Object>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Entry<Object, Object> entry = entries.next();
            Object key = entry.getKey(); // we are assuming string KEYS
            Object value = entry.getValue();
			Object element = doCopy(value, baseInterface);
            newMap.put(key,element);
        }
        return newMap;
    }

    private static final Set<Class<?>> BASIC_TYPES = new HashSet<Class<?>>();

    static {
        BASIC_TYPES.add(Boolean.class);
        BASIC_TYPES.add(Byte.class);
        BASIC_TYPES.add(Character.class);
        BASIC_TYPES.add(Double.class);
        BASIC_TYPES.add(Float.class);
        BASIC_TYPES.add(Integer.class);
        BASIC_TYPES.add(Long.class);
        BASIC_TYPES.add(Short.class);
        BASIC_TYPES.add(String.class);
        BASIC_TYPES.add(Date.class);
    }

    private boolean isBasicType(Class<?> vClass) {
        return BASIC_TYPES.contains(vClass);
    }

}
