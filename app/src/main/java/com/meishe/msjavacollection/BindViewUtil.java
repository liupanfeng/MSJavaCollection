package com.meishe.msjavacollection;

import android.app.Activity;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class BindViewUtil {

    /**
     * 通过反射和注解 自动绑定View
     * @param activity
     */
    public static void init(Activity activity){

        Class<? extends Activity> aClass = activity.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        if (declaredFields!=null&&declaredFields.length>0){
            for (int i = 0; i < declaredFields.length; i++) {
                Field field = declaredFields[i];
                if (field.isAnnotationPresent(MSInjectView.class)) {
                    MSInjectView annotation = field.getAnnotation(MSInjectView.class);
                    if (annotation!=null){
                        int value = annotation.value();
                        View viewById = activity.findViewById(value);
                        field.setAccessible(true);
                        try {
                            field.set(activity,viewById);
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
    public static void injectEvent(Activity activity){

        Class<? extends Activity> aClass = activity.getClass();
        Method[] declaredMethods = aClass.getDeclaredMethods();
        if (declaredMethods.length>0){
            for (Method method : declaredMethods) {
                if (method.isAnnotationPresent(MSBindClick.class)){
                    MSBindClick msBindClick = method.getAnnotation(MSBindClick.class);
                    Class<? extends Annotation> annotationType = msBindClick.annotationType();
                    if (annotationType.isAnnotationPresent(MSEventType.class)){
                        MSEventType annotation = annotationType.getAnnotation(MSEventType.class);
                        Class listenerType = annotation.listenerType();
                        String setOnCLickListener = annotation.listenerSet();


                        method.setAccessible(true);
                        ListenerInvocationHandler<Activity> handler=new ListenerInvocationHandler<>(activity,method);
                        Object listenerProxy = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[]{listenerType}, handler);
                        int[] ids = msBindClick.value();
                        for (int id : ids) {
                            View viewById = activity.findViewById(id);
                            try {
                                Method method1 = viewById.getClass().getMethod(setOnCLickListener, listenerType);
                                method1.invoke(viewById,listenerProxy);
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



    static class ListenerInvocationHandler<T> implements InvocationHandler{

        private T target;

        private Method method;

        public ListenerInvocationHandler(T target, Method method) {
            this.target = target;
            this.method = method;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] args) throws Throwable {
            return this.method.invoke(target,args);
        }
    }



}
