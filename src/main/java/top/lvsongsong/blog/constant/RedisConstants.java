package top.lvsongsong.blog.constant;

/**
 * @author LvSS
 * @create 2018/06/28 11:34
 */
public class RedisConstants {
    /**
     * 文章索引列表前缀 hash
     */
    public final static String REDIS_ARTILES = "articles";
    /**
     * 文章分类  list
     */
    public final static String REDIS_ARTICLE_CLASSIFICATIONS = "classifications";
    /**
     * 文章访问计数 zset
     */
    public final static String REDIS_ARTICLE_VIEWS = "views";
    /**
     * 文章发布时间  zset
     */
    public final static String REDIS_ARTICLE_TIME = "time";
}
