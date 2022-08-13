package com.meishe.compile.agent;

import com.meishe.compile.IMessage;

public class Agent implements IMessage {

    private final IMessage massage;

    public Agent(IMessage massage) {
        this.massage = massage;
    }


    //....前置处理
    public void before() {
        System.out.println("特效渲染前");
    }

    //....后置处理
    public void after() {
        System.out.println("特效渲染后----");
    }

    @Override
    public void showMessage() {
        before();
        massage.showMessage();
        after();
    }
}
