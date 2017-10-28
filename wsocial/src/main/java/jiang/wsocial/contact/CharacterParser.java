/*
 * Filename	CharacterParser.java
 * Company	上海乐问-浦东分公司。
 * @author	LuRuihui
 * @version	0.1
 */
package jiang.wsocial.contact;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author yun.
 * @date 2015/10/22.
 * @des [一句话描述]
 * @since [https://github.com/ZuYun]
 */
public class CharacterParser {
    private static final String TAG = "CharacterParser";
    private StringBuilder buffer;
    private String resource;

    private CharacterParser(){

    }

    private static class Inner {
        public static CharacterParser sCharacterParser = new CharacterParser();
    }

    public static CharacterParser getInstance(){
        return Inner.sCharacterParser;
    }

    public String getResource(){
        return resource;
    }

    public void setResource(String resource){
        this.resource = resource;
    }

    /** 汉字转成ASCII码 * * @param chs * @return */
    private int getChsAscii(String chs){
        int asc = 0;
        try {
            byte[] bytes = chs.getBytes("gb2312");
            if(bytes.length>2 || bytes.length<=0) {
                throw new RuntimeException("illegal resource string");
            }
            if(bytes.length == 1) {
                asc = bytes[0];
            }
            if(bytes.length == 2) {
                int hightByte = 256+bytes[0];
                int lowByte = 256+bytes[1];
                asc = ( 256*hightByte+lowByte )-256*256;
            }
        }catch(Exception e) {
            Log.e(TAG, "ERROR:ChineseSpelling.class-getChsAscii(String chs)"+e);
        }
        return asc;
    }

    /**
     * 单字解析
     *
     * @param str 江
     * @return jiang
     */
    public String convert(String str){
        return HanziToPinyin.getPinYin(str).toUpperCase();
    }

    /** 词组解析
     * @param chs 祖国
     * @return  zuguo
     *
     */
    public String getSelling(String chs){
        String key, value;
        buffer = new StringBuilder();
        for(int i = 0; i<chs.length(); i++) {
            key = chs.substring(i, i+1);
            if(key.getBytes().length>=2) {
                value = (String)convert(key);
                if(value == null) {
                    value = "unknown";
                }
            }else {
                value = key;
            }
            buffer.append(value);
        }
        return buffer.toString();
    }


    /**
     * 返回 词组首字字母
     */
    public String getInitials(String chs){
        return convert(chs.trim().substring(0, 1)).substring(0, 1);
    }

    /**
     * 返回 词组首字母组合
     */
    public String getAcronym(String chs){
        char[] chars = chs.trim().toCharArray();
        StringBuilder acronym = new StringBuilder();
        for(char aChar : chars) {
            acronym.append(convert(aChar+"").charAt(0));
        }
        return acronym.toString();
    }

    public String getSpelling(){
        return this.getSelling(this.getResource());
    }

    /**
     * 支持全拼音 连续拆分匹配
     * 支持汉子 连续拆分匹配
     *
     * @param str
     *         需要匹配的 字符串
     * @param key
     *         匹配的关键字 可以是英文
     * @param partMatch
     *         是否支持拆分匹配
     * @return 支持匹配的关键字 集合
     */
    public List<String> getMatchString(String str, String key, boolean partMatch){
        str = str.toLowerCase();
        key = key.toLowerCase();
        ArrayList<String> matchss = new ArrayList<>();
        //找出匹配的字符
        if(key.matches("[a-zA-Z]+")) {
            //关键字全为英文
            //1,连续匹配 可能有多个
            String acronym = getAcronym(str);//将需要匹配的字符串转为首字母字符串
            int indexOf = 0;
            int keyLength = key.length();
            String acronymSub = acronym;
            String strSub = str;
            while(indexOf+keyLength<=acronymSub.length()) {//可能有多个（中国总过中国中国）--zg...//也满足单匹配 z
                if(acronymSub.contains(key)) {
                    indexOf = acronymSub.indexOf(key);
                    String match = strSub.substring(indexOf, indexOf+keyLength);
                    if(!matchss.contains(match)) {//如果匹配的集合里面没有就加到里面
                        matchss.add(match);
                    }
                }
                acronymSub = acronymSub.substring(indexOf+keyLength, acronymSub.length());
                strSub = strSub.substring(indexOf+keyLength, strSub.length());
            }
            //2，拆开 分开匹配  (中国国中工作)--zg
            if(partMatch) {
                ArrayList<String> matchssTemp = new ArrayList<>();
                HashSet machedChar = new HashSet();
                char[] charsKey = key.toCharArray();
                for(int i = 0; i<charsKey.length; i++) {
                    char[] chars = str.toCharArray();
                    if(machedChar.size()<i) {
                        break;
                    }
                    for(char aChar : chars) {
                        String convert = convert(aChar+"");//小写拼音
                        if(convert.startsWith(charsKey[i]+"")) {
                            machedChar.add(charsKey[i]);//有匹配则 添加到已匹配set中
                            if(!matchssTemp.contains(aChar+"")) {
                                matchssTemp.add(aChar+"");
                            }
                        }
                    }
                    if(i == charsKey.length-1 && machedChar.size() == charsKey.length) {//当拆开的每个字母都有匹配时
                        matchss.addAll(matchssTemp);
                    }
                }
            }
        }else {
            //非全英文
            //连续匹配
            if(str.contains(key)) {
                matchss.add(key);
            }
            if(partMatch) {
                //拆分匹配
                ArrayList<String> matchssTemp = new ArrayList<>();
                HashSet machedChar = new HashSet();
                char[] charsKey = key.toCharArray();
                for(int i = 0; i<charsKey.length; i++) {
                    char[] chars = str.toCharArray();
                    if(machedChar.size()<i) {
                        break;
                    }
                    for(char aChar : chars) {
                        if(( aChar+"" ).equals(charsKey[i]+"")) {
                            machedChar.add(charsKey[i]);//有匹配则 添加到已匹配set中
                            if(!matchssTemp.contains(aChar+"")) {
                                matchssTemp.add(aChar+"");
                            }
                        }
                    }
                    if(i == charsKey.length-1 && machedChar.size() == charsKey.length) {//当拆开的每个字母都有匹配时
                        matchss.addAll(matchssTemp);
                    }
                }
            }
        }
        return matchss;
    }

    public void setHighlight(TextView tv, String str, String search, String color, boolean partMatch){

        List<String> matchss = new ArrayList<>();
        SpannableString spanStr = new SpannableString(str);
        //        matchss.addAll(getMatchString(str, search, matchss)};
        matchss = getMatchString(str, search, partMatch);

        for(String matchs : matchss) {
            //关键字高亮
            String args = matchs;//需要高亮的关键字
            Pattern p = Pattern.compile(matchs, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(spanStr);
            while(m.find()) {
                int start = m.start();
                int end = m.end();
                spanStr.setSpan(new ForegroundColorSpan(Color.parseColor(color)), start, end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        tv.setText(spanStr);
    }

}
