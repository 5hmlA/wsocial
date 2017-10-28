package jiang.wsocial.emoji.help;

/**
 * @another 江祖赟
 * @date 2017/9/14 0014.
 */
public class Smiley {
    public static interface EMOJI_ORIGN{
     String HTTP= "http";
     String HTTPS="https";
     String FILE="file";
     String CONTENT="content";
     String ASSETS="assets";
     String DRAWABLE="drawable";
     String UNKNOWN="no";

    }
    public String name;
    public String iconPath;
    public String content;
    public String from = EMOJI_ORIGN.ASSETS;

    public Smiley(){
    }

    public Smiley(String name, String iconPath, String from){
        this.name = name;
        this.iconPath = iconPath;
        this.from = from;
    }

    public Smiley(String name, String iconPath, String content, String from){
        this.name = name;
        this.iconPath = iconPath;
        this.content = content;
        this.from = from;
    }

    public Smiley(String name, String iconPath){
        this.name = name;
        this.iconPath = iconPath;
        this.from = from;
    }
}
