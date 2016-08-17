/**
 * Created by An0nymous on 16/8/17.
 */
public class CrackIdeaPluginFactory {
    //使用 getShape 方法获取形状类型的对象
    public CrackIdeaPlugin getCrackIdeaPlugin(String crackIdeaPluginType){
        if(crackIdeaPluginType == null){
            return null;
        }
        if(crackIdeaPluginType.equalsIgnoreCase("Mybatis Plugin")){
            return new MybatisPlugin();
        } else if(crackIdeaPluginType.equalsIgnoreCase("Markdown Navigator")){
            return new MarkdownNavigator();
        }
        return null;
    }
}
