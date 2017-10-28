package jiang.wsocial.contact;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.SparseArray;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @another 江祖赟
 * @date 2017/9/18 0018.
 */
public class JContactBean extends EditAble implements Comparable<JContactBean> {

    public String jcName;
    public String jcInitials;
    public String jcFlag = "n";
    public static final String COLLECTED = "\u2606";
    public static final String COLLECTEDSHOW = "特别关注";
    public static final String UNKNOWN = "#";

    public JContactBean(){
    }

    public JContactBean(String name){
        jcName = name;
        getInitial(jcName);
    }

    public void getInitial(String str){
        jcInitials = CharacterParser.getInstance().getInitials(str);
        Matcher matcher = Pattern.compile("^[^a-zA-Z]+$").matcher(jcInitials);
        if(matcher.find()) {
            jcInitials = UNKNOWN;
        }
    }

    public JContactBean addFlag(){
        return this;
    }

    public JContactBean add2Collect(){
        jcFlag = COLLECTED;
        jcInitials = COLLECTEDSHOW;
        return this;
    }

    @Override
    public int compareTo(@NonNull JContactBean o){
        boolean flagc;
        if(( flagc = jcFlag.equals(COLLECTED) )^o.jcFlag.equals(COLLECTED)) {
            return flagc ? -1 : 1;
        }

        boolean flag;
        if(!TextUtils.isEmpty(jcInitials) &&( flag = jcInitials.startsWith("#") )^o.jcInitials.startsWith("#")) {
            return flag ? 1 : -1;
        }
        return jcInitials.compareTo(o.jcInitials);
    }


    public static SparseArray<String> buildHeaderList(List<JContactBean> contactList){
        Collections.sort(contactList);
        SparseArray<String> headList = new SparseArray<>();
        headList.put(0, contactList.get(0).jcInitials);
        for(int i = 1; i<contactList.size(); i++) {
            if(!contactList.get(i-1).jcInitials.equalsIgnoreCase(contactList.get(i).jcInitials)) {
                headList.put(i, contactList.get(i).jcInitials.trim());
            }
        }
        return headList;
    }

    public static LinkedHashMap<Integer,String> buildHeaderMap(List<? extends JContactBean> contactList){
        Collections.sort(contactList);
        LinkedHashMap<Integer,String> headMap = new LinkedHashMap<Integer,String>();
        headMap.put(0, contactList.get(0).jcInitials);
        for(int i = 1; i<contactList.size(); i++) {
            if(!contactList.get(i-1).jcInitials.equalsIgnoreCase(contactList.get(i).jcInitials)) {
                headMap.put(i, contactList.get(i).jcInitials.trim());
            }
        }
        return headMap;
    }
    //
    //    public static LinkedHashMap<Integer,String> buildHeaderMap(List<Object> contactList){
    //        Collections.sort(contactList);
    //        LinkedHashMap<Integer,String> headMap = new LinkedHashMap<Integer,String>();
    //        headMap.put(0, contactList.get(0).jcInitials);
    //        for(int i = 1; i<contactList.size(); i++) {
    //            if(!contactList.get(i-1).jcInitials.equalsIgnoreCase(contactList.get(i).jcInitials)) {
    //                headMap.put(i, contactList.get(i).jcInitials.trim());
    //            }
    //        }
    //        return headMap;
    //    }

}
