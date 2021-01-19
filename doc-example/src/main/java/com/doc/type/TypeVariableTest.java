package com.doc.type;

import java.io.Serializable;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by haifei on 19/1/2021 2:04 AM.
 */
public class TypeVariableTest<T, V extends @Custom Number & Serializable> {
    private Number number;
    public T t;
    public V v;
    public List<T> list = new ArrayList<>();
    public Map<String, T> map = new HashMap<>();

    public T[] tArray;
    public List<T>[] ltArray;

    public TypeVariableTest testClass;
    public TypeVariableTest<T, Integer> testClass2;

    public Map<? super String, ? extends Number> mapWithWildcard;

    //泛型构造函数,泛型参数为X
    public <X extends Number> TypeVariableTest(X x, T t) {
        number = x;
        this.t = t;
    }

    //泛型方法，泛型参数为Y
    public <Y extends T> void method(Y y) {
        t = y;
    }

    public static void main(String[] args) throws NoSuchFieldException {
        //****************************TypeVariable************************
        Field v = TypeVariableTest.class.getField("v");//用反射的方式获取属性 public V v;
        TypeVariable typeVariable = (TypeVariable) v.getGenericType();//获取属性类型
        System.out.println("TypeVariable1:" + typeVariable);
        System.out.println("TypeVariable2:" + Arrays.asList(typeVariable.getBounds()));//获取类型变量上界
        System.out.println("TypeVariable3:" + typeVariable.getGenericDeclaration());//获取类型变量声明载体
        //1.8 AnnotatedType: 如果这个这个泛型参数类型的上界用注解标记了，我们可以通过它拿到相应的注解
        AnnotatedType[] annotatedTypes = typeVariable.getAnnotatedBounds();
        List<AnnotatedType> annotatedTypes1 = Arrays.asList(annotatedTypes);
        System.out.println(annotatedTypes1.get(0).getType());
        System.out.println(annotatedTypes1.get(1).getType());
        System.out.println("TypeVariable4:" + annotatedTypes1 + " : " + Arrays.asList(
                annotatedTypes[0].getAnnotations()));

        System.out.println("TypeVariable5:" + typeVariable.getName());
    }
}