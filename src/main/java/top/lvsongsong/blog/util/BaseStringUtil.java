package top.lvsongsong.blog.util;

import java.util.UUID;

/**
 * @author LvSS
 * @create 2018/05/16 18:03
 */
public class BaseStringUtil {
    /**
     * 获取uuid
     *
     * @return
     */
    public static String getUid() {
        String s = UUID.randomUUID().toString();
        return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
    }


}
