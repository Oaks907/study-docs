[TOC]

## ParameterizedType参数化类型
```
interface ParameterizedType extends Type {
     //获取参数类型<>里面的那些值,例如Map<K,V> 那么就得到 [K,V]的一个数组
     Type[] getActualTypeArguments(); 
     //获取参数类型<>前面的值，例如例如Map<K,V> 那么就得到 Map
     Type getRawType();
     //获取其父类的类型，例如Map 有一个内部类Entry,  那么在Map.Entry<K,V> 上调用这个方法就可以获得 Map
     Type getOwnerType();
}
```
### 方法说明：

* getRowType
  获取当前`ParameterizedType`的类型，如果是一个List，返回的是List的Type，即返回参数化本身的Type。

* getOwnerType
  获取其父类的类型，例如Map 有一个内部类Entry,  那么在Map.Entry<K,V> 上调用这个方法就可以获得 Map。

  这里应该是针对内部类的。

* getActualTypeArguments
该方法返回参数化类型<>中的实际参数类型， 如 `Map<String,Person> map` 这个 `ParameterizedType` 返回的是 String 类,Person 类的全限定类名的 Type Array。注意: 该方法只返回最外层的<>中的类型，无论该<>内有多少个<>。
###  示例：
```
public class TestParameterizedTypeBean<T> {

    //是ParameterizedType
    private HashMap<String, Object> map;
    private HashSet<String> set;
    private List<String> list;
    private Class<?> clz;

    //不是ParameterizedType
    private Integer i;
    private String str;

    private static void printParameterizedType() {
        Field[] fields = TestParameterizedTypeBean.class.getDeclaredFields();
        for (Field f : fields) {
            //打印是否是ParameterizedType类型
            System.out.println("FieldName:  " + f.getName() + " instanceof ParameterizedType is : "
                                       + (f.getGenericType() instanceof ParameterizedType));
        }
        //取map这个类型中的实际参数类型的数组
        getParameterizedTypeWithName("map");
        getParameterizedTypeWithName("str");
    }

    private static void getParameterizedTypeWithName(String name) {
        Field f;
        try {
            //利用反射得到TestParameterizedTypeBean类中的所有变量
            f = TestParameterizedTypeBean.class.getDeclaredField(name);
            f.setAccessible(true);
            Type type = f.getGenericType();
            if (type instanceof ParameterizedType) {
                for (Type param : ((ParameterizedType) type).getActualTypeArguments()) {
                    //打印实际参数类型
                    System.out.println("---type actualType---" + param.toString());
                }
                //打印所在的父类的类型
                System.out.println("---type ownerType0---" + ((ParameterizedType) type).getOwnerType());
                //打印其本身的类型
                System.out.println("---type rawType---" + ((ParameterizedType) type).getRawType());
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        printParameterizedType();
    }

}

```
返回值：
```
FieldName:  map instanceof ParameterizedType is : true
FieldName:  set instanceof ParameterizedType is : true
FieldName:  list instanceof ParameterizedType is : true
FieldName:  clz instanceof ParameterizedType is : true
FieldName:  i instanceof ParameterizedType is : false
FieldName:  str instanceof ParameterizedType is : false
---type actualType---class java.lang.String
---type actualType---class java.lang.Object
---type ownerType0---null
---type rawType---class java.util.HashMap
```

说明：

1、 getOwnerType 获取的是内部类父类的参数化类型。当前Map不是某个类的内部类，故获取到的数据为null。

## TypeVariable 类型变量

### 方法说明：

```
interface TypeVariable<D extends GenericDeclaration> extends Type, AnnotatedElement {
    //返回此类型参数的上界列表，如果没有上界则放回Object. 例如  V extends @Custom Number & Serializable 这个类型参数，有两个上界，Number 和 Serializable 
    Type[] getBounds();
    //类型参数声明时的载体，例如 `class TypeTest<T, V extends @Custom Number & Serializable>` ，那么V 的载体就是TypeTest
    D getGenericDeclaration();
    String getName();
    //Java 1.8加入 AnnotatedType: 如果这个这个泛型参数类型的上界用注解标记了，我们可以通过它拿到相应的注解
    AnnotatedType[] getAnnotatedBounds();
}
```

### 示例

```
public class TypeTest<T, V extends @Custom Number & Serializable> {
    private Number number;
    public T t;
    public V v;
    public List<T> list = new ArrayList<>();
    public Map<String, T> map = new HashMap<>();

    public T[] tArray;
    public List<T>[] ltArray;

    public TypeTest testClass;
    public TypeTest<T, Integer> testClass2;

    public Map<? super String, ? extends Number> mapWithWildcard;

    //泛型构造函数,泛型参数为X
    public <X extends Number> TypeTest(X x, T t) {
        number = x;
        this.t = t;
    }

    //泛型方法，泛型参数为Y
    public <Y extends T> void method(Y y) {
        t = y;
    }

    public static void main(String[] args) throws NoSuchFieldException {
        //****************************TypeVariable************************
        Field v = TypeTest.class.getField("v");//用反射的方式获取属性 public V v;
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
```

返回值：

```
TypeVariable1:V
TypeVariable2:[class java.lang.Number, interface java.io.Serializable]
TypeVariable3:class com.doc.type.TypeTest
class java.lang.Number
interface java.io.Serializable
TypeVariable4:[sun.reflect.annotation.AnnotatedTypeFactory$AnnotatedTypeBaseImpl@66d3c617, sun.reflect.annotation.AnnotatedTypeFactory$AnnotatedTypeBaseImpl@63947c6b] : [@com.doc.type.Custom()]
TypeVariable5:V
```

## GenericArrayType：泛型数组类型

### 方法说明：

```
public interface GenericArrayType extends Type {
    //获取泛型类型数组的声明类型，即获取数组方括号 [] 前面的部分
    Type getGenericComponentType();
}
```

`GenericArrayType` 接口只有一个方法getGenericComponentType()，其可以用来获取数组方括号 **[]** 前面的部分，例如`T[]`，在其上调用getGenericComponentType 就可以获得`T`. 值得注意的是多维数组得到的是最后一个[] 前面的部分，例如`T[][]`, 得到的是`T[]`.

### 示例：

```
public class GenericArrayTypeTest<T, V extends @Custom Number & Serializable> {

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
    public <X extends Number> GenericArrayTypeTest(X x, T t) {
        number = x;
        this.t = t;
    }

    //泛型方法，泛型参数为Y
    public <Y extends T> void method(Y y) {
        t = y;
    }

    public static void main(String[] args) throws NoSuchFieldException {
        //**********************GenericArrayType*********************
        Field tArray = GenericArrayTypeTest.class.getField("tArray");
        System.out.println("数组参数类型1:" + tArray.getGenericType());
        Field ltArray = GenericArrayTypeTest.class.getField("ltArray");
        System.out.println("数组参数类型2:" + ltArray.getGenericType());//数组参数类型2:java.util.List<T>[]
        if (tArray.getGenericType() instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType) tArray.getGenericType();
            System.out.println("数组参数类型3:" + arrayType.getGenericComponentType());//数组参数类型3:T
        }
        if (ltArray.getGenericType() instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType) ltArray.getGenericType();
            System.out.println("数组参数类型4:" + arrayType.getGenericComponentType());
        }
    }
}
```

返回值：

```
数组参数类型1:T[]
数组参数类型2:java.util.List<T>[]
数组参数类型3:T
数组参数类型4:java.util.List<T>
```

## **WildcardType: 通配符类型**

### 方法说明：

```
public interface WildcardType extends Type {
   // 获取上界
    Type[] getUpperBounds();
    //获取下界
    Type[] getLowerBounds();
}
```

### 示例：

```
public class WildcardTypeTest<T, V extends @Custom Number & Serializable> {
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
    public <X extends Number> WildcardTypeTest(X x, T t) {
        number = x;
        this.t = t;
    }

    //泛型方法，泛型参数为Y
    public <Y extends T> void method(Y y) {
        t = y;
    }

    public static void main(String[] args) throws NoSuchFieldException {
        //***************************WildcardType*********************************
        Field mapWithWildcard = WildcardTypeTest.class.getField("mapWithWildcard");
        Type wild = mapWithWildcard.getGenericType();//先获取属性的泛型类型 Map<? super String, ? extends Number>
        if (wild instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) wild;
            Type[] actualTypes = pType.getActualTypeArguments();//获取<>里面的参数变量 ? super String, ? extends Number
            System.out.println("WildcardType1:" + Arrays.asList(actualTypes));
            WildcardType first = (WildcardType) actualTypes[0];//? super java.lang.String
            WildcardType second = (WildcardType) actualTypes[1];//? extends java.lang.Number
            System.out.println("WildcardType2: lower:" + Arrays.asList(first.getLowerBounds()) + "  upper:" + Arrays.asList(first.getUpperBounds()));//WildcardType2: lower:[class java.lang.String]  upper:[class java.lang.Object]
            System.out.println("WildcardType3: lower:" + Arrays.asList(second.getLowerBounds()) + "  upper:" + Arrays.asList(second.getUpperBounds()));//WildcardType3: lower:[]  upper:[class java.lang.Number]
        }
    }
}
```

返回值说明：

```
WildcardType1:[? super java.lang.String, ? extends java.lang.Number]
WildcardType2: lower:[class java.lang.String]  upper:[class java.lang.Object]
WildcardType3: lower:[]  upper:[class java.lang.Number]
```

资料参考：
1. [Java中的Type类型详解](https://juejin.cn/post/6844903597977632776)
2. [秒懂Java类型（Type）系统](https://zhuanlan.zhihu.com/p/64584427)