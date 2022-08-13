package com.meishe.msjavacollection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.meishe.msjavacollection.inject.MSAutowired;
import com.meishe.msjavacollection.inject.MSBindClick;
import com.meishe.msjavacollection.inject.MSEventType;
import com.meishe.msjavacollection.inject.MSInjectView;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class BindViewUtil {

    /**
     * 通过反射和注解 自动绑定View
     *
     * @param activity
     */
    public static void injectView(Activity activity) {

        Class<? extends Activity> aClass = activity.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        if (declaredFields != null && declaredFields.length > 0) {
            for (int i = 0; i < declaredFields.length; i++) {
                Field field = declaredFields[i];
                if (field.isAnnotationPresent(MSInjectView.class)) {
                    MSInjectView annotation = field.getAnnotation(MSInjectView.class);
                    if (annotation != null) {
                        int value = annotation.value();
                        View viewById = activity.findViewById(value);
                        field.setAccessible(true);
                        try {
                            field.set(activity, viewById);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }


    /**
     * 自动绑定点击事件
     */
    public static void injectEvent(Activity activity) {
        /*得到class*/
        Class<? extends Activity> aClass = activity.getClass();
        /*得到class所有的方法*/
        Method[] declaredMethods = aClass.getDeclaredMethods();

        if (declaredMethods.length > 0) {
            for (Method method : declaredMethods) {
                /*如果方法上有 MSBindClick 注解 */
                if (method.isAnnotationPresent(MSBindClick.class)) {
                    /*得到MSBindClick 注解对象*/
                    MSBindClick msBindClick = method.getAnnotation(MSBindClick.class);
                    /*得到注解的class类对象*/
                    Class<? extends Annotation> annotationType = msBindClick.annotationType();
                    /*判断注解上边是否有MSEventType 注解 */
                    if (annotationType.isAnnotationPresent(MSEventType.class)) {
                        /*得到MSEventType 注解*/
                        MSEventType annotation = annotationType.getAnnotation(MSEventType.class);
                        /*得到View.OnClickListener 接口*/
                        Class listenerType = annotation.listenerType();
                        /*得到注解上的内容 第一个是方法名setOnClickListener*/
                        String setOnCLickListener = annotation.listenerSet();

                        /*设置方法访问权限*/
                        method.setAccessible(true);
                        /*
                         * 动态代理的回调方法：需要回调的是activity的 被注解的方法
                         * 第一个参数是activity
                         * 第二个参数是onClick方法
                         * */
                        ListenerInvocationHandler<Activity> handler = new ListenerInvocationHandler<>(activity, method);
                        /*
                         * 这里代理的就是View.OnClickListener 接口
                         * listenerType：View.OnClickListener 接口  代理的内容是OnClickListener这个接口
                         * */
                        Object listenerProxy = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[]{listenerType}, handler);
                        int[] ids = msBindClick.value();
                        for (int id : ids) {
                            View viewById = activity.findViewById(id);
                            try {
//                                setOnCLickListener  就是字符串"setOnCLickListener"  这个是方法名称
//                                第二个是方法参数 view.OnClickListener
//                               通过调用方法的invoke方法  执行setOnClickListener()
//                              invoke方法的第一个参数是：方法属于哪个对象  第二个是参数
                                Method method1 = viewById.getClass().getMethod(setOnCLickListener, listenerType);
                                method1.invoke(viewById, listenerProxy);
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                }
            }
        }
    }


    static class ListenerInvocationHandler<T> implements InvocationHandler {

        private T target;

        private Method method;

        public ListenerInvocationHandler(T target, Method method) {
            this.target = target;   //目前第一个参数是activity
            this.method = method;   //第二个参数是onClick方法
        }

        /*
         *第一个参数：
         *第二个参数：method: public abstract void android.view.View$OnClickListener.onClick(android.view.View)
         * 第三个参数就是：点击的view控件对象
         * */
        @Override
        public Object invoke(Object o, Method method, Object[] args) throws Throwable {
            return this.method.invoke(target, args);
        }
    }


    public static void injectAutowired(Activity activity) {
        Class<? extends Activity> aClass = activity.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        if (declaredFields.length > 0) {
            Intent intent = activity.getIntent();
            Bundle extras = intent.getExtras();
            for (Field field : declaredFields) {
                if (field.isAnnotationPresent(MSAutowired.class)) {
                    MSAutowired annotation = field.getAnnotation(MSAutowired.class);
                    String value = annotation.value();
                    String key = TextUtils.isEmpty(value) ? field.getName() : value;

                    if (!TextUtils.isEmpty(key)) {  //有value的情况
                        Object o = extras.get(key);
                        field.setAccessible(true);
                        try {
//                            Parcelable[] 不能直接设置会崩溃
                            Class<?> componentType = field.getType().getComponentType();

                            Log.d("lpf", "field.getType=" + field.getType());
                            Log.d("lpf", "field.getType().getComponentType();=" + componentType);
                            Log.d("lpf", "field.getGenericType()=" + field.getGenericType());
                            Log.d("lpf", "------------------------------------------------------------------");

                            if (field.getType().isArray() && componentType.isAssignableFrom(Parcelable.class)) {
                                Object[] obj = (Object[]) o;
                                Object[] objects = Arrays.copyOf(obj, obj.length, (Class<? extends Object[]>) field.getType());
                                o = objects;
                            }
                            field.set(activity, o);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


}
