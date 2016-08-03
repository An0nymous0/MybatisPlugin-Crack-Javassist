import javassist.*;

import java.io.IOException;

/**
 * Created by An0nymous on 16/7/4.
 */
public class Crack {
    public static void main(String[] args) {

        try {
            ///////////////////////////////////
            //  使用javaassist修改 class/jar 代码
            ///////////////////////////////////
            //  设置jar包路径
            ClassPool.getDefault().insertClassPath("/Users/An0nymous/Documents/Software/IDEA Pluges/2.84/mybatis_plus.jar");
            // 获取需要修改的类
            CtClass javaUtilsClass = ClassPool.getDefault().getCtClass("com.seventh7.mybatis.util.JavaUtils");
            // 获取类中的refValid方法
            CtMethod refValidMethod = javaUtilsClass.getDeclaredMethod("refValid");
            // 修改该方法的内容
            refValidMethod.setBody("{ validated = true; valid = true; return valid; }");
            // 删除valid和validated静态变量
            CtField validField = javaUtilsClass.getDeclaredField("valid");
            javaUtilsClass.removeField(validField);
            CtField validatedField = javaUtilsClass.getDeclaredField("validated");
            javaUtilsClass.removeField(validatedField);
            // 修改初始化valid和validated变量为true
            CtField f2 = CtField.make("private static boolean valid = true;", javaUtilsClass);
            CtField f3 = CtField.make("private static boolean validated = true;", javaUtilsClass);
            javaUtilsClass.addField(f2);
            javaUtilsClass.addField(f3);
            // 写出到外存中
            javaUtilsClass.writeFile();

            // 获取需要修改的类
            CtClass javaServiceClass = ClassPool.getDefault().getCtClass("com.seventh7.mybatis.service.JavaService");
            // 获取类中的stop方法
            CtMethod stopMethod = javaServiceClass.getDeclaredMethod("stop");
            // 修改该方法的内容
            stopMethod.setBody("{ this.stopped = false; }");
            javaServiceClass.writeFile();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
