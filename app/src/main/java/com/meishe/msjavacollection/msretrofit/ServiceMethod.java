package com.meishe.msjavacollection.msretrofit;

import com.meishe.msjavacollection.msretrofit.annotation.Field;
import com.meishe.msjavacollection.msretrofit.annotation.GET;
import com.meishe.msjavacollection.msretrofit.annotation.POST;
import com.meishe.msjavacollection.msretrofit.annotation.Query;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * 采用建造者设计模式，进行设计
 *
 * 建造者设计模式：
 * 类的属性  一般都会在建造者里边声明
 * 构造方法将建造者对象传递进去
 * build方法会对：1.如果没传递给默认值
 * 2.必须的内容，没传递直接throw exception
 */
public class ServiceMethod {

    /*通过调用newCall 将request传进去 得到Call 对象*/
    private final Call.Factory callFactory;
    /*相对地址  拼上base地址就得到一个完成的url*/
    private final String relativeUrl;
    /*是否有请求body  post有  get没有*/
    private final boolean hasBody;
    /*管理请求参数*/
    private final ParameterHandler[] parameterHandler;

    /*用于创建请求体*/
    private FormBody.Builder formBuild;

    HttpUrl baseUrl;
    /*请求方式*/
    String httpMethod;

    HttpUrl.Builder urlBuilder;



    public ServiceMethod(Builder builder) {
        /*通过builder对属性进行赋值操作*/
        this.hasBody=builder.hasBody;
        this.baseUrl=builder.msRetrofit.baseUrl;
        this.callFactory=builder.msRetrofit.callFactory;
        this.httpMethod= builder.httpMethod;
        this.relativeUrl= builder.relativeUrl;
        this.parameterHandler= builder.parameterHandler;

        //如果是有请求体,创建一个okhttp的请求体对象
        if (hasBody){
            formBuild=new FormBody.Builder();
        }
    }


    /**
     * get 请求参数
     * @param key
     * @param value
     */
    public void addQueryParameter(String key, String value) {
        if (urlBuilder==null){
            urlBuilder=baseUrl.newBuilder(relativeUrl);
        }
        urlBuilder.addQueryParameter(key,value);
    }



    public void addFiledParameter(String key, String value){
        formBuild.add(key,value);
    }

    /*静态内部类*/
    public static class Builder {

        private final MSRetrofit msRetrofit;

        private final Annotation[] methodAnnotations;

        private final Annotation[][] parameterAnnotations;

        ParameterHandler[] parameterHandler;

        private String httpMethod;
        private String relativeUrl;
        private boolean hasBody;

        public Builder(MSRetrofit msRetrofit, Method method) {
            this.msRetrofit=msRetrofit;
            /*得到方法上的注解*/
            methodAnnotations=method.getAnnotations();
            /*得到参数类型的注解*/
            parameterAnnotations=method.getParameterAnnotations();
        }

        public ServiceMethod build() {

            /**
             * 1 解析方法上的注解, 只处理POST与GET
             */
            for (Annotation methodAnnotation : methodAnnotations) {
                if (methodAnnotation instanceof POST){
                    this.httpMethod="POST";
                    this.relativeUrl=((POST) methodAnnotation).value();
                    this.hasBody=true;
                }else if (methodAnnotation instanceof GET){
                    this.httpMethod="GET";
                    this.relativeUrl=((GET) methodAnnotation).value();
                    this.hasBody=false;
                }
            }


            int length = parameterAnnotations.length;
            parameterHandler = new ParameterHandler[length];
            for (int i = 0; i < length; i++) {
                Annotation[] annotations = parameterAnnotations[i];
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Field) {
                        //得到注解上的value: 请求参数的key
                        String value = ((Field) annotation).value();
                        /*对请求参数进行保存*/
                        parameterHandler[i]= new ParameterHandler.FiledParameterHandler(value);
                    }else if (annotation instanceof Query){
                        String value = ((Query) annotation).value();
                        /*对请求参数进行保存*/
                        parameterHandler[i] = new ParameterHandler.QueryParameterHandler(value);
                    }
                }
            }

            return new ServiceMethod(this);
        }
    }

    public Object invoke(Object[] args) {
        /**
         * 1  处理请求的地址与参数
         */
        for (int i = 0; i < parameterHandler.length; i++) {
            ParameterHandler handlers = parameterHandler[i];
            //handler内本来就记录了key,现在给到对应的value
            handlers.apply(this, args[i].toString());
        }


        //获取最终请求地址
        HttpUrl url;
        if (urlBuilder == null) {
            urlBuilder = baseUrl.newBuilder(relativeUrl);
        }

        url=urlBuilder.build();

        //请求体
        FormBody formBody = null;
        if (formBuild != null) {
            formBody = formBuild.build();
        }

        Request request=new Request.Builder().url(url).method(httpMethod,formBody).build();

        return callFactory.newCall(request);
    }
}
