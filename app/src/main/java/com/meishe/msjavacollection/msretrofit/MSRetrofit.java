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
        ServiceMethod result = serviceMethodCache.get(method);
        if (result!=null){
            return result;
        }

        synchronized (serviceMethodCache){
            result=serviceMethodCache.get(method);
            if (result==null){
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
