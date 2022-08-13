package com.meishe.compile.statics;


import com.meishe.compile.IMessage;
import com.meishe.compile.IEffect;

/**
 *  实实现类： Avatar 特效
 */
public class Avatar implements IMessage, IEffect {



    @Override
    public void render() {
        System.out.println("Avatar----渲染特效");
    }

    @Override
    public void showMessage() {
        System.out.println("Avatar----渲染中");
    }
}
