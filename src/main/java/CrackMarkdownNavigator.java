import Utils.VersionUtil;
import javassist.*;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by An0nymous on 16/7/4.
 */
public class CrackMarkdownNavigator {

    /**
     *  Markdown Navigator
     * 下面版本号一定要填写
     * 如:private static final String version = "2.0.0";
     **/
    private static final String version = "2.0.0";

    public static void main(String[] args) throws Exception {
        versionValidation();
        try {
            ///////////////////////////////////
            //  使用javaassist修改 class/jar 代码
            ///////////////////////////////////
            ClassPool pool = ClassPool.getDefault();

            String libPath = System.getProperty("user.dir") + "/lib";
            String jarName = "idea-multimarkdown.jar";
            Path copy_from = Paths.get(libPath, jarName);
            Path copy_to = Paths.get(System.getProperty("user.dir"), "crack_" + jarName);
            try {
                Files.copy(copy_from, copy_to, REPLACE_EXISTING, COPY_ATTRIBUTES, NOFOLLOW_LINKS);
            } catch (IOException e) {
                System.err.println(e);
            }
            pool.insertClassPath(libPath + "/" + jarName);

            // 获取需要修改的类
            crackOtherMethodNoWriteFile(pool, "com.vladsch.idea.multimarkdown.license.LicenseAgent", "isActivationExpired", "{return false;}");
            crackOtherMethodNoWriteFile(pool, "com.vladsch.idea.multimarkdown.license.LicenseAgent", "isValidLicense", "{return true;}");
            crackOtherMethodNoWriteFile(pool, "com.vladsch.idea.multimarkdown.license.LicenseAgent", "getLicenseExpiringIn", "{return 666;}");
            CtClass  javaServiceClass = crackOtherMethodNoWriteFile(pool, "com.vladsch.idea.multimarkdown.license.LicenseAgent", "isValidActivation", "{return true;}");
            CtField validField = javaServiceClass.getDeclaredField("license_type");
            javaServiceClass.removeField(validField);
            CtField f2 = CtField.make("private String license_type = \"license\";", javaServiceClass);
            javaServiceClass.addField(f2);
            javaServiceClass.writeFile();
            exeCmd("jar uvf " + System.getProperty("user.dir") + "/" + "crack_idea-multimarkdown.jar com/vladsch/idea/multimarkdown/license/LicenseAgent.class");
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void crackOtherMethod(ClassPool pool, String classname, String methodName, String source) throws NotFoundException, CannotCompileException, IOException {
        // 获取需要修改的类
        CtClass javaServiceClass = pool.getCtClass(classname);
        // 获取类中的stop方法
        CtMethod stopMethod = javaServiceClass.getDeclaredMethod(methodName);
        // 修改该方法的内容
        stopMethod.setBody(source);
        javaServiceClass.writeFile();
    }

    private static CtClass crackOtherMethodNoWriteFile(ClassPool pool, String classname, String methodName, String source) throws NotFoundException, CannotCompileException, IOException {
        // 获取需要修改的类
        CtClass javaServiceClass = pool.getCtClass(classname);
        // 获取类中的stop方法
        CtMethod stopMethod = javaServiceClass.getDeclaredMethod(methodName);
        // 修改该方法的内容
        stopMethod.setBody(source);
        return javaServiceClass;
    }

    private static void versionValidation() throws Exception {
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


}
