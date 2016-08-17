/**
 * Created by An0nymous on 16/8/17.
 */
public class Crack {
    private static final String MYBATIS_PLUGIN = "Mybatis Plugin"; // https://www.codesmagic.com/
    private static final String MARKDOWN_NAVIGATOR = "Markdown Navigator"; // http://github.com/vsch/idea-multimarkdown

    public static void main(String[] args) throws Exception {

        /********************************必填*************************************/
        String puginName = MARKDOWN_NAVIGATOR; //MARKDOWN_NAVIGATOR|MYBATIS_PLUGIN
        String version = "2.0.0"; //软件版本如2.0.0
        /********************************必填*************************************/

        CrackIdeaPluginFactory cipf = new CrackIdeaPluginFactory();
        CrackIdeaPlugin cip = cipf.getCrackIdeaPlugin(puginName);//软件名必填
        cip.crack(version);//版本号必填
        System.out.println("破解成功,复制项目根目录下crack_开头文件,替换对应插件源文件重启idea即可");
    }
}
