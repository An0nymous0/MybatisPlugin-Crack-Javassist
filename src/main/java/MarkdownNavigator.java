import Utils.CrackUtil;
import javassist.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by An0nymous on 16/8/17.
 */
public class MarkdownNavigator implements CrackIdeaPlugin {
    @Override
    public void crack(String version) throws Exception {
        CrackUtil.versionValidation(version);
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
            CrackUtil.crackOtherMethodNoWriteFile(pool, "com.vladsch.idea.multimarkdown.license.LicenseAgent", "isActivationExpired", "{return false;}");
            CrackUtil.crackOtherMethodNoWriteFile(pool, "com.vladsch.idea.multimarkdown.license.LicenseAgent", "isValidLicense", "{return true;}");
            CrackUtil.crackOtherMethodNoWriteFile(pool, "com.vladsch.idea.multimarkdown.license.LicenseAgent", "getLicenseExpiringIn", "{return 666;}");
            CtClass javaServiceClass = CrackUtil.crackOtherMethodNoWriteFile(pool, "com.vladsch.idea.multimarkdown.license.LicenseAgent", "isValidActivation", "{return true;}");

            javaServiceClass.removeField(javaServiceClass.getDeclaredField("license_type"));
            CtField f2 = CtField.make("private String license_type = \"license\";", javaServiceClass);
            javaServiceClass.addField(f2);

            javaServiceClass.removeField(javaServiceClass.getDeclaredField("license_expires"));
            CtField f3 = CtField.make("private String license_expires = \"2026-08-03\";", javaServiceClass);
            javaServiceClass.addField(f3);

            javaServiceClass.removeField(javaServiceClass.getDeclaredField("license_features"));
            CtField f4 = CtField.make("private int license_features = \"1\";", javaServiceClass);
            javaServiceClass.addField(f4);
            javaServiceClass.writeFile();
            CrackUtil.exeCmd("jar uvf " + System.getProperty("user.dir") + "/" + "crack_idea-multimarkdown.jar com/vladsch/idea/multimarkdown/license/LicenseAgent.class");
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
}
