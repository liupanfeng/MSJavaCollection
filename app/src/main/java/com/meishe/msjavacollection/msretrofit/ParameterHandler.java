package com.meishe.msjavacollection.msretrofit;

public  abstract class ParameterHandler {


    abstract void apply(ServiceMethod serviceMethod, String toString);

     static class FiledParameterHandler extends ParameterHandler {
         String key;

         public FiledParameterHandler(String key) {
             this.key = key;
         }

         @Override
         void apply(ServiceMethod serviceMethod, String value) {
             serviceMethod.addFiledParameter(key,value);
         }
    }

     static class QueryParameterHandler extends ParameterHandler {
         String key;
         public QueryParameterHandler(String key) {
             this.key = key;

         }

         @Override
         void apply(ServiceMethod serviceMethod, String value) {
             serviceMethod.addQueryParameter(key,value);
         }
    }
}
