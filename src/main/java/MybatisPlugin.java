import Utils.CrackUtil;
import Utils.VersionUtil;
import javassist.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

/**
 * Created by An0nymous on 16/8/17.
 */
public class MybatisPlugin implements CrackIdeaPlugin {
    @Override
    public void crack(String version) throws Exception {
        CrackUtil.versionValidation(version);
        try {
            ///////////////////////////////////
            //  使用javaassist修改 class/jar 代码
            ///////////////////////////////////
            ClassPool pool = ClassPool.getDefault();

            String libPath = System.getProperty("user.dir") + "/lib";
            String jarName = "mybatis_plus.jar";
            Path copy_from = Paths.get(libPath, jarName);
            Path copy_to = Paths.get(System.getProperty("user.dir"), "crack_" + jarName);
            try {
                Files.copy(copy_from, copy_to, REPLACE_EXISTING, COPY_ATTRIBUTES, NOFOLLOW_LINKS);
            } catch (IOException e) {
                System.err.println(e);
            }

            pool.insertClassPath(libPath + "/" + jarName);

            CrackUtil.crackJavaUtils(pool);
            CrackUtil.exeCmd("jar uvf " + System.getProperty("user.dir") + "/" + "crack_mybatis_plus.jar com/seventh7/mybatis/service/JavaService.class");

            CrackUtil.crackOtherMethod(pool, "com.seventh7.mybatis.service.JavaService", "stop", "{ this.stopped = false; }");
            CrackUtil.exeCmd("jar uvf " + System.getProperty("user.dir") + "/" + "crack_mybatis_plus.jar com/seventh7/mybatis/util/JavaUtils.class");

            if (VersionUtil.compareVersion(version, "2.84") >= 0) {
                if (VersionUtil.compareVersion(version, "2.84") == 0) {
                    CrackUtil.crackOtherMethod(pool, "com.seventh7.mybatis.dom.model.Completion", "run", "while(true) {\n" +
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
                    CrackUtil.exeCmd("jar uvf " + System.getProperty("user.dir") + "/" + "crack_mybatis_plus.jar com/seventh7/mybatis/dom/model/Completion.class");
                }

                CrackUtil.crackOtherMethod(pool, "com.seventh7.mybatis.ref.license.ActivationDriver", "activate", "{if(org.apache.commons.lang.StringUtils.isBlank($1)) {\n" +
                        "            return com.seventh7.mybatis.ref.license.ActivationResult.fail(\"License key invalid\");\n" +
                        "        } else {\n" +
                        "            com.intellij.openapi.util.Ref ref = com.intellij.openapi.util.Ref.create();\n" +
                        "            com.seventh7.mybatis.ref.license.LicenseData result = new com.seventh7.mybatis.ref.license.LicenseData(\"123\", \"123\");\n" +
                        "            ref.set(com.seventh7.mybatis.ref.license.ActivationResult.success(result));\n" +
                        "            return (com.seventh7.mybatis.ref.license.ActivationResult)ref.get();\n" +
                        "        }\n" +
                        "    }");
                CrackUtil.exeCmd("jar uvf " + System.getProperty("user.dir") + "/" + "crack_mybatis_plus.jar com/seventh7/mybatis/ref/license/ActivationDriver.class");


            }
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
