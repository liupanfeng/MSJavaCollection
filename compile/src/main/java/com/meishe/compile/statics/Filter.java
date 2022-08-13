package com.meishe.compile.statics;


import com.meishe.compile.IMessage;

/**
 * 真实实现类： 滤镜
 */
public class Filter implements IMessage {
    @Override
    public void showMessage() {
        System.out.println("滤镜特效添加中------");
    }
}
