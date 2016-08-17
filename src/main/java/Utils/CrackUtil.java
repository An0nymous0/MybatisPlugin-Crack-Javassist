package Utils;

import javassist.*;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by An0nymous on 16/8/17.
 */
public class CrackUtil {

    public static void versionValidation(String version) throws Exception {
        if (!StringUtils.isNotBlank(version)) {
            throw new Exception("请填写破解插件版本号！");
        }
    }

    public static void exeCmd(String commandStr) {
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec(commandStr);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            System.out.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static CtClass crackOtherMethodNoWriteFile(ClassPool pool, String classname, String methodName, String source) throws NotFoundException, CannotCompileException, IOException {
        // 获取需要修改的类
        CtClass javaServiceClass = pool.getCtClass(classname);
        // 获取类中的stop方法
        CtMethod stopMethod = javaServiceClass.getDeclaredMethod(methodName);
        // 修改该方法的内容
        stopMethod.setBody(source);
        return javaServiceClass;
    }

    public static void crackJavaUtils(ClassPool pool) throws NotFoundException, CannotCompileException, IOException {
        // 获取需要修改的类
        CtClass javaUtilsClass = pool.getCtClass("com.seventh7.mybatis.util.JavaUtils");
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
    }

    public static void crackOtherMethod(ClassPool pool, String classname, String methodName, String source) throws NotFoundException, CannotCompileException, IOException {
        // 获取需要修改的类
        CtClass javaServiceClass = pool.getCtClass(classname);
        // 获取类中的stop方法
        CtMethod stopMethod = javaServiceClass.getDeclaredMethod(methodName);
        // 修改该方法的内容
        stopMethod.setBody(source);
        javaServiceClass.writeFile();
    }
}
