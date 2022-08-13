package com.meishe.msjavacollection.msretrofit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class MSRetrofit {

    /*缓存*/
    final Map<Method,ServiceMethod> serviceMethodCache =new ConcurrentHashMap<>();

    final Call.Factory callFactory;

    final HttpUrl baseUrl;

    public MSRetrofit(Call.Factory callFactory, HttpUrl baseUrl) {
        this.callFactory = callFactory;
        this.baseUrl = baseUrl;
    }

    public <T> T create(final Class<T> service){

//
//
        /*
        *  这里代理的是MSWeatherApi 这个接口  这个接口里边定义了很多请求的方法
        *  请求的方法上面就包含了：
        *  请求的方式：GET or POST
        *  请求的参数：key value对的方式来保存的
        *  请求的相对地址：/v3/weather/weatherInfo  这个点
        *  通过动态代理的方式：相当于是一个切面 统一拿到这些信息
        * */
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //解析这个method 上所有的注解信息
                ServiceMethod serviceMethod = loadServiceMethod(method);
                return serviceMethod.invoke(args);
            }
        });
    }

    private ServiceMethod loadServiceMethod(Method method) {
        /*先从缓存里边获取*/
        ServiceMethod result = serviceMethodCache.get(method);
        if (result!=null){
            return result;
        }

        synchronized (serviceMethodCache){
            result=serviceMethodCache.get(method);
            if (result==null){
                /*如果缓存没有 再通过new的方式创建  ServiceMethod 也使用了建造者设计模式  将retrofit对象和通过动态代理得到的method 传递过去*/
                result=new ServiceMethod.Builder(this,method).build();
                serviceMethodCache.put(method,result);
            }
        }
        return result;
    }


    /**
     * 构建者模式，将一个复杂对象的构建和它的表示分离，可以使使用者不必知道内部组成的细节。
     */
    public static final class Builder {
        private HttpUrl baseUrl;

        //Okhttp->OkhttClient
        private okhttp3.Call.Factory callFactory;

        public Builder callFactory(okhttp3.Call.Factory factory) {
            this.callFactory = factory;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = HttpUrl.get(baseUrl);
            return this;
        }

        public MSRetrofit build() {
            if (baseUrl == null) {
                throw new IllegalStateException("Base URL required.");
            }

            okhttp3.Call.Factory callFactory = this.callFactory;

            if (callFactory == null) {
                callFactory = new OkHttpClient();
            }

            return new MSRetrofit(callFactory,baseUrl);
        }
    }

}
