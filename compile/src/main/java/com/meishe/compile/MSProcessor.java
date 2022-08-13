package com.meishe.compile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * 注解处理器，编译期生成代码  字节码增强技术
 */

@SupportedAnnotationTypes({"com.meishe.msjavacollection.MSInjectView"})
public class MSProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Messager messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE,"--------------lpf so cool------------------"+set.size());
        if (!set.isEmpty()){
            try {
                JavaFileObject sourceFile=processingEnv.getFiler().createSourceFile("com.meishe.Render");
                OutputStream os=sourceFile.openOutputStream();
                os.write("package com.meishe; \n public class Render{}".getBytes());
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}