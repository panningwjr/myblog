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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author LvSS
 * @create 2018/05/11 18:02
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    /**
     * 文章索引列表前缀
     */
    private final static String REDIS_ARTILES = "articles";
    /**
     * 文章分类
     */
    private final static String REDIS_ARTICLE_CLASSIFICATIONS = "classifications";

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private ArticleService articleService;

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
        for (Map.Entry entry : classifications.entrySet()) {
            ListOperations<Object, Object> listOperations = this.redisTemplate.opsForList();
            size = listOperations.size(REDIS_ARTILES + ":" + entry.getKey());
            List<Object> articleVOS = listOperations.range(REDIS_ARTILES + ":" + entry.getKey(), 0, size);
            classifyArticles.put((String) entry.getValue(), articleVOS);
        }
        return classifyArticles;
    }

    @RequestMapping("/classifications")
    public Map getClassifications() {
        return this.redisTemplate.opsForHash().entries(REDIS_ARTICLE_CLASSIFICATIONS);
    }

    @RequestMapping("/addclassification")
    public String addArticleClassify(String classify, String classifyName) {
        this.redisTemplate.opsForHash().put(REDIS_ARTICLE_CLASSIFICATIONS, classify, classifyName);
        return "success";
    }

    @RequestMapping("/add")
    public String createArticle(Article article) {
        try {
            article.setArticleId(BaseStringUtil.getUid());
            this.articleService.saveArticle(article);
            ArticleVO articleVO = new ArticleVO();
            articleVO.setId(article.getArticleId());
            articleVO.setTitle(article.getTitle());
            articleVO.setClassification(article.getClassify());
            this.redisTemplate.opsForList().rightPush(REDIS_ARTILES + ":" + article.getClassify(), articleVO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    @RequestMapping("/html/{id}")
    public ModelAndView selectArticle(@PathVariable String id) {
        ModelAndView modelAndView = new ModelAndView("/articles/article_template");
        Article article = this.articleService.findOneByID(id);
        modelAndView.addObject("article", article);
        return modelAndView;
    }
}
