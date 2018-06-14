package top.lvsongsong.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import top.lvsongsong.blog.modal.Article;
import top.lvsongsong.blog.service.ArticleService;
import top.lvsongsong.blog.util.BaseStringUtil;
import top.lvsongsong.blog.vo.ArticleVO;

import java.util.*;

/**
 * @author LvSS
 * @create 2018/05/11 18:02
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    /**
     * 文章索引列表前缀 hash
     */
    private final static String REDIS_ARTILES = "articles";
    /**
     * 文章分类  list
     */
    private final static String REDIS_ARTICLE_CLASSIFICATIONS = "classifications";
    /**
     * 文章访问计数 zset
     */
    private final static String REDIS_ARTICLE_VIEWS = "views";
    /**
     * 文章发布时间  zset
     */
    private final static String REDIS_ARTICLE_TIME = "time";

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private ArticleService articleService;

    /**
     * 最近的5篇文章
     *
     * @return
     */
    @RequestMapping("/recent/{limit}")
    public List<ArticleVO> recentArticles(@PathVariable Integer limit) {
        limit = (limit == null) ? 9 : (limit - 1);
        Set<Object> articleids = this.redisTemplate.opsForZSet().reverseRange(REDIS_ARTICLE_TIME, 0, limit);
        HashOperations<Object, Object, ArticleVO> hashOperations = this.redisTemplate.opsForHash();
        List<ArticleVO> articleVOS = hashOperations.multiGet(REDIS_ARTILES, articleids);
        return articleVOS;
    }

    /**
     * 最受欢迎的文章
     *
     * @return
     */
    public String mostPopularArticles() {
        return "";
    }

    /**
     * 获取文章列表,文章目录
     *
     * @return
     */
    @RequestMapping("/indexs")
    public Map<String, List<Object>> allArticles() {
        HashOperations<Object, Object, Object> hashOperations = this.redisTemplate.opsForHash();
        Map<Object, Object> classifications = hashOperations.entries(REDIS_ARTICLE_CLASSIFICATIONS);
        TreeMap<String, List<Object>> classifyArticles = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        Long size;
        //对文章分类进行固定顺序的排序
        for (Map.Entry entry : classifications.entrySet()) {
            ListOperations<Object, Object> listOperations = this.redisTemplate.opsForList();
            size = listOperations.size(REDIS_ARTILES + ":" + entry.getKey());
            //获取每个分类队列里的文章
            List<Object> articleids = listOperations.range(REDIS_ARTILES + ":" + entry.getKey(), 0, size);
            //根据文章id的list从 hash中一次性取出所有的文章
            List<Object> articleVOS = hashOperations.multiGet(REDIS_ARTILES, articleids);
            classifyArticles.put((String) entry.getValue(), articleVOS);
        }
        return classifyArticles;
    }

    /**
     * 获取文章分类
     *
     * @return
     */
    @RequestMapping("/classifications")
    public Map getClassifications() {
        return this.redisTemplate.opsForHash().entries(REDIS_ARTICLE_CLASSIFICATIONS);
    }

    /**
     * 添加文章分类
     *
     * @param classify
     * @param classifyName
     * @return
     */
    @RequestMapping("/addclassification")
    public String addArticleClassify(String classify, String classifyName) {
        this.redisTemplate.opsForHash().put(REDIS_ARTICLE_CLASSIFICATIONS, classify, classifyName);
        return "success";
    }

    /**
     * 保存新增文章
     *
     * @param article
     * @return
     */
    @RequestMapping("/add")
    public String createArticle(Article article) {
        try {
            article.setArticleId(BaseStringUtil.getUid());
            Date current = new Date();
            article.setCreatetime(current);
            article.setModifytime(current);
            this.articleService.saveArticle(article);
            ArticleVO articleVO = new ArticleVO();
            articleVO.setId(article.getArticleId());
            articleVO.setTitle(article.getTitle());
            articleVO.setClassification(article.getClassify());
            articleVO.setCreateTime(current);
            articleVO.setModifyTime(current);
            //记录文章的发布时间
            this.redisTemplate.opsForZSet().add(REDIS_ARTICLE_TIME, articleVO.getId(), current.getTime());
            //向redis 中添加文章记录hash articles: id-article
            this.redisTemplate.opsForHash().put(REDIS_ARTILES, articleVO.getId(), articleVO);
            //添加文章分类队列 list  articles:jvm id
            this.redisTemplate.opsForList().rightPush(REDIS_ARTILES + ":" + article.getClassify(), articleVO.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    /**
     * 访问文章
     *
     * @param id
     * @return
     */
    @RequestMapping("/html/{id}")
    public ModelAndView selectArticle(@PathVariable String id) {
        ModelAndView modelAndView = new ModelAndView("/articles/article_template");
        //文章访问计数+1
        this.redisTemplate.opsForZSet().incrementScore(REDIS_ARTICLE_VIEWS, id, 1);
        Article article = this.articleService.findOneByID(id);
        modelAndView.addObject("article", article);
        modelAndView.addObject("views", this.redisTemplate.opsForZSet().score(REDIS_ARTICLE_VIEWS, id));
        return modelAndView;
    }
}
