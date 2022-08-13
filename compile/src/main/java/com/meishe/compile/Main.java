package com.meishe.compile;

import com.meishe.compile.statics.Avatar;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Main {
    public static void main(String[] args) {
        System.out.println("----------------------------------------------");
        //静态代理
//        IMessage message=new Alvin();
//        Agent agent=new Agent(message);
//        agent.message();


//        动态代理
        final Avatar avatar = new Avatar();

        Object o = Proxy.newProxyInstance(Main.class.getClassLoader(), new Class[]{IMessage.class, IEffect.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                return method.invoke(avatar, objects);
            }
        });
        System.out.println(o.getClass().getName());
        IMessage massage = (IMessage) o;
        massage.showMessage();

        IEffect IEffect = (IEffect) o;
        IEffect.render();

    }
}
