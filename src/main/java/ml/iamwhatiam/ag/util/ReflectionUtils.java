/**
 * MIT License
 * 
 * Copyright (c) 2017 iMinusMinus
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ml.iamwhatiam.ag.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 反射工具类
 * 
 * @author iMinusMinus
 * @since 2017-09-07
 */
public class ReflectionUtils {

    private static Logger log = LoggerFactory.getLogger(ReflectionUtils.class);

    private ReflectionUtils() {

    }

    /**
     * 获取泛型实际类信息
     * 
     * @param javaType
     * @return
     */
    public static Class<?> type2class(Type javaType) {
        if (javaType instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) javaType).getActualTypeArguments();
            return type2class(types[types.length - 1]);
        } else if (javaType instanceof TypeVariable) {
            return Object.class;
        } else if (javaType instanceof GenericArrayType) {
            return type2class(((GenericArrayType) javaType).getGenericComponentType());
        }
        return (Class<?>) javaType;
    }

    /**
     * @see #invoke(Method, Object, Object...)
     * @param methodName 方法名
     * @param klazz 类名
     * @param parameterTypes 参数名
     * @param target 对象
     * @param args 参数
     * @return 方法调用结果
     */
    public static Object invoke(String methodName, String klazz, Class[] parameterTypes, Object target,
                                Object... args) {
        Class<?> klz = findClass(klazz);
        Method method = findAccessibleMethod(methodName, klz, parameterTypes);
        return invoke(method, target, args);
    }

    /**
     * 使用指定参数调用指定对象的指定方法
     * 
     * @param method 方法, NotNull
     * @param target 对象, NotNull. 静态方法传入类，否则传入实例
     * @param args 参数
     * @return 方法调用结果
     */
    public static Object invoke(Method method, Object target, Object... args) {
        Object obj = target;
        try {
            if (!method.isAccessible()) {
                log.debug("method [{}.{}] cann't access under class '{}'", method.getDeclaringClass(), method.getName(),
                        target instanceof Class ? target : target.getClass());
                method.setAccessible(true);
            }
            if ((method.getModifiers() & Modifier.STATIC) != 0) {
                obj = method.getDeclaringClass();
            }
            return method.invoke(obj, args);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询指定类（使用当前类的类加载器）
     * 
     * @param klazz 类名
     * @return 类，或null
     */
    public static Class<?> findClass(String klazz) {
        Class<?> find = null;
        try {
            find = Class.forName(klazz);
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        return find;
    }

    /**
     * 查询指定类（使用指定的类加载器）
     * 
     * @param klazz 类名
     * @return 类，或null
     */
    public static Class<?> findClass(String klazz, ClassLoader loader) {
        Class<?> find = null;
        try {
            find = Class.forName(klazz, true, loader);
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        return find;
    }

    /**
     * 获取指定方法，查找顺序：自身所有方法，父类及祖类受保护方法和公有方法
     * 
     * @param methodName 方法名
     * @param klazz 方法所在类
     * @param parameterTypes 方法参数类型
     * @return 搜索到的方法，或null
     */
    public static Method findAccessibleMethod(String methodName, Class<?> klazz, Class<?>... parameterTypes) {
        Class<?> self = klazz;
        int acceptModifiers = Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC;
        do {
            Method[] methods = self.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName) && Arrays.equals(method.getParameterTypes(), parameterTypes)
                        && isAccessibleModifier(method, acceptModifiers, klazz)) {
                    return method;
                }
            }
            acceptModifiers ^= Modifier.PRIVATE;
            self = self.getSuperclass();
        } while (self.getSuperclass() != Object.class);
        log.error("cannot access method [{}] with paramters [{}] under class [{}]", methodName, parameterTypes, klazz);
        return null;
    }

    /**
     * 搜索方法，传入的参数类型可以是方法实际参数类型的子类
     * 
     * @see #findAccessibleMethod(String, Class, Class...)
     * @param methodName 方法名
     * @param klazz 方法所在类
     * @param parameterTypes 方法参数类型
     * @return 搜索到的方法列表，或空列表
     */
    public static List<Method> findCompitableAccessibleMethod(String methodName, Class<?> klazz,
                                                              Class<?>... parameterTypes) {
        List<Method> maybeMethods = new ArrayList<Method>();
        Class<?> self = klazz;
        int acceptModifiers = Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC;
        do {
            Method[] methods = self.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName) && isCompitable(method.getParameterTypes(), parameterTypes)
                        && (method.getModifiers() & acceptModifiers) != 0) {
                    maybeMethods.add(method);
                }
            }
            acceptModifiers ^= Modifier.PRIVATE;
            self = self.getSuperclass();
        } while (self.getSuperclass() != Object.class);
        return maybeMethods;
    }

    private static boolean isCompitable(Class<?>[] origin, Class<?>[] compare) {
        if (origin.length != compare.length) {
            return false;
        }
        for (int i = 0; i < origin.length; i++) {
            if (!origin[i].isAssignableFrom(compare[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean isAccessibleModifier(Method method, int acceptModifiers, Class<?> klazz) {
        return ((method.getModifiers() & acceptModifiers) != 0)
                || (method.getDeclaringClass() == klazz && method.getModifiers() == Modifier.STATIC);
    }

}
