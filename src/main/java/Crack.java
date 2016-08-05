import javassist.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

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
            ClassPool pool = ClassPool.getDefault();

            String libPath=System.getProperty("user.dir")+"/lib";
            String jarName="mybatis_plus.jar";
            Path copy_from = Paths.get(libPath,jarName);
            Path copy_to= Paths.get(System.getProperty("user.dir"),"crack_"+jarName);
            try {
                Files.copy(copy_from, copy_to, REPLACE_EXISTING, COPY_ATTRIBUTES, NOFOLLOW_LINKS);
            } catch (IOException e) {
                System.err.println(e);
            }

            pool.insertClassPath(libPath+"/"+jarName);

            /** 1.修改JavaUtils **/
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

            /** 2.修改JavaService **/
            // 获取需要修改的类
            CtClass javaServiceClass = pool.getCtClass("com.seventh7.mybatis.service.JavaService");
            // 获取类中的stop方法
            CtMethod stopMethod = javaServiceClass.getDeclaredMethod("stop");
            // 修改该方法的内容
            stopMethod.setBody("{ this.stopped = false; }");
            javaServiceClass.writeFile();

            /** 3.修改Completion **/
            // 获取需要修改的类
            CtClass completionClass = pool.getCtClass("com.seventh7.mybatis.dom.model.Completion");
//            // 获取类中的stop方法
            CtMethod runMethod = completionClass.getDeclaredMethod("run");
//            // 修改该方法的内容
            runMethod.setBody("while(true) {\n" +
                    "            try {\n" +
                    "                String var11 = com.intellij.openapi.application.PathManager.getOptionsPath() + java.io.File.separator + \"mybatis.xml\";\n" +
                    "                java.io.File file = new java.io.File(var11);\n" +
                    "                if(file.exists() && !file.isDirectory()) {\n" +
                    "                    com.seventh7.mybatis.setting.MybatisSetting service = (com.seventh7.mybatis.setting.MybatisSetting)com.intellij.openapi.components.ServiceManager.getService(com.seventh7.mybatis.setting.MybatisSetting.class);\n" +
                    "                    org.jdom.Element element = service.getState();\n" +
                    "                    element.setAttribute(\"KEY\", \"mockKey\");\n" +
                    "                    element.setAttribute(\"Insert\", \"1\");\n" +
                    "                    service.loadState(element);\n" +
                    "                    return;\n" +
                    "                }\n" +
                    "                return;\n" +
                    "            } catch (Exception var5) {\n" +
                    "                ;\n" +
                    "            }\n" +
                    "        }");
            completionClass.writeFile();

            /** 4.修改ActivationDriver **/
            // 获取需要修改的类
            CtClass activationDriverClass = pool.getCtClass("com.seventh7.mybatis.ref.license.ActivationDriver");
            // 获取类中的stop方法
            CtMethod activateMethod = activationDriverClass.getDeclaredMethod("activate");
            // 修改该方法的内容
            activateMethod.setBody("{if(org.apache.commons.lang.StringUtils.isBlank($1)) {\n" +
                    "            return com.seventh7.mybatis.ref.license.ActivationResult.fail(\"License key invalid\");\n" +
                    "        } else {\n" +
                    "            com.intellij.openapi.util.Ref ref = com.intellij.openapi.util.Ref.create();\n" +
                    "            com.seventh7.mybatis.ref.license.LicenseData result = new com.seventh7.mybatis.ref.license.LicenseData(\"123\", \"123\");\n" +
                    "            ref.set(com.seventh7.mybatis.ref.license.ActivationResult.success(result));\n" +
                    "            return (com.seventh7.mybatis.ref.license.ActivationResult)ref.get();\n" +
                    "        }\n" +
                    "    }");
            activationDriverClass.writeFile();


            replaceJarFiles();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void replaceJarFiles(){
        exeCmd("jar uvf "+System.getProperty("user.dir")+"/"+"crack_mybatis_plus.jar com/seventh7/mybatis/dom/model/Completion.class");
        exeCmd("jar uvf "+System.getProperty("user.dir")+"/"+"crack_mybatis_plus.jar com/seventh7/mybatis/ref/license/ActivationDriver.class");
        exeCmd("jar uvf "+System.getProperty("user.dir")+"/"+"crack_mybatis_plus.jar com/seventh7/mybatis/service/JavaService.class");
        exeCmd("jar uvf "+System.getProperty("user.dir")+"/"+"crack_mybatis_plus.jar com/seventh7/mybatis/util/JavaUtils.class");
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
        }
        finally
        {
            if (br != null)
            {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
