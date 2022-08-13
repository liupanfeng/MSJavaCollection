package com.meishe.msjavacollection.inject;


import android.view.View;

import androidx.annotation.IdRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *自动绑定点击事件
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@MSEventType(listenerType = View.OnClickListener.class,listenerSet="setOnClickListener")
public @interface MSBindClick {

   @IdRes int[] value();

}
