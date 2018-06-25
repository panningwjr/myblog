package top.lvsongsong.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import top.lvsongsong.blog.modal.Article;
import top.lvsongsong.blog.service.ArticleService;
import top.lvsongsong.blog.util.BaseStringUtil;
import top.lvsongsong.blog.vo.ArticleVO;
import top.lvsongsong.blog.vo.ClassifyVO;

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
    private RedisTemplate<String, ? extends Object> redisTemplate;

    @Autowired
    private ArticleService articleService;

    /**
     * 获取最近的n篇文章
     *
     * @return
     */
    @RequestMapping("/recent/{limit}")
    public List<ArticleVO> recentArticles(@PathVariable Integer limit) {
        limit = (limit == null) ? 9 : (limit - 1);
        Set<String> articleids = (Set<String>) this.redisTemplate.opsForZSet().reverseRange(REDIS_ARTICLE_TIME, 0, limit);
        HashOperations<String, String, ArticleVO> hashOperations = this.redisTemplate.opsForHash();
        List<ArticleVO> articleVOS = hashOperations.multiGet(REDIS_ARTILES, articleids);
        this.handleArticleIcon(articleVOS);
        return articleVOS;
    }

    /**
     * 根据文章分类设置文章的图标
     *
     * @param articleVOS
     */
    private void handleArticleIcon(List<ArticleVO> articleVOS) {
        List<ClassifyVO> classifyVOS = this.getClassifications();
        Map<String, String> classifyIcons = new HashMap<>(classifyVOS.size());
        for (ClassifyVO classifyVO : classifyVOS) {
            classifyIcons.put(classifyVO.getCode(), classifyVO.getIcon());
        }
        for (ArticleVO articleVO : articleVOS) {
            articleVO.setIcon(classifyIcons.get(articleVO.getClassification()));
        }
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
    public Map<String, List<ArticleVO>> allArticles() {
        HashOperations<String, String, ArticleVO> hashOperations = this.redisTemplate.opsForHash();
        ListOperations<String, ClassifyVO> classifyOperations = (ListOperations<String, ClassifyVO>) this.redisTemplate.opsForList();
        ListOperations<String, String> articleListOperations = (ListOperations<String, String>) this.redisTemplate.opsForList();
        Long size = classifyOperations.size(REDIS_ARTICLE_CLASSIFICATIONS);
        List<ClassifyVO> classifications = classifyOperations.range(REDIS_ARTICLE_CLASSIFICATIONS, 0, size);
        Map<String, List<ArticleVO>> articles = new LinkedHashMap<>(size.intValue());
        for (ClassifyVO classifyVO : classifications) {
            Long articleSize = articleListOperations.size(REDIS_ARTILES + ":" + classifyVO.getCode());
            //获取每个分类队列里的文章
            List<String> articleids = articleListOperations.range(REDIS_ARTILES + ":" + classifyVO.getCode(), 0, articleSize);
            //根据文章id的list从 hash中一次性取出所有的文章
            List<ArticleVO> articleVOS = hashOperations.multiGet(REDIS_ARTILES, articleids);
            articles.put(classifyVO.getName() + "," + classifyVO.getIcon(), articleVOS);
        }
        return articles;
    }

    /**
     * 删除分类
     *
     * @param index
     * @return
     */
    @RequestMapping("/classifications/delete/{index}")
    public String deleteClassifications(@PathVariable("index") Long index) {
        if (index > 0) {
            ListOperations<String, ClassifyVO> operations = (ListOperations<String, ClassifyVO>) this.redisTemplate.opsForList();
            ClassifyVO classifyVO = operations.index(REDIS_ARTICLE_CLASSIFICATIONS, index);
            operations.remove(REDIS_ARTICLE_CLASSIFICATIONS, index, classifyVO);
            return "success";
        } else {
            return "error";
        }
    }

    /**
     * 修改分类
     *
     * @param classifyVO
     * @param index
     * @return
     */
    @RequestMapping("/classifications/update/{index}")
    public String modifyClassifications(ClassifyVO classifyVO, @PathVariable("index") Long index) {
        ListOperations<String, ClassifyVO> operations = (ListOperations<String, ClassifyVO>) this.redisTemplate.opsForList();
        if (index < 0) {
            operations.rightPush(REDIS_ARTICLE_CLASSIFICATIONS, classifyVO);
        } else {
            operations.set(REDIS_ARTICLE_CLASSIFICATIONS, index, classifyVO);
        }
        return "success";
    }

    /**
     * 获取文章分类
     *
     * @return
     */
    @RequestMapping("/classifications")
    public List<ClassifyVO> getClassifications() {
        ListOperations<String, ClassifyVO> operations = (ListOperations<String, ClassifyVO>) this.redisTemplate.opsForList();
        Long len = operations.size(REDIS_ARTICLE_CLASSIFICATIONS);
        List<ClassifyVO> clas = operations.range(REDIS_ARTICLE_CLASSIFICATIONS, 0, len);
        return clas;
    }

    /**
     * 添加文章分类
     *
     * @param classify
     * @return
     */
    @RequestMapping("/addclassification")
    public String addArticleClassify(ClassifyVO classify) {
        ListOperations<String, ClassifyVO> listOperations = (ListOperations<String, ClassifyVO>) this.redisTemplate.opsForList();
        listOperations.rightPush(REDIS_ARTICLE_CLASSIFICATIONS, classify);
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
            ZSetOperations<String, String> zSetOperations = (ZSetOperations<String, String>) this.redisTemplate.opsForZSet();
            zSetOperations.add(REDIS_ARTICLE_TIME, articleVO.getId(), current.getTime());
            //向redis 中添加文章记录hash articles: id-article
            this.redisTemplate.opsForHash().put(REDIS_ARTILES, articleVO.getId(), articleVO);
            //添加文章分类队列 list  articles:jvm id
            ListOperations<String, String> listOperations = (ListOperations<String, String>) this.redisTemplate.opsForList();
            listOperations.rightPush(REDIS_ARTILES + ":" + article.getClassify(), articleVO.getId());
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
        ZSetOperations<String, String> zSetOperations = (ZSetOperations<String, String>) this.redisTemplate.opsForZSet();
        zSetOperations.incrementScore(REDIS_ARTICLE_VIEWS, id, 1);
        Article article = this.articleService.findOneByID(id);
        modelAndView.addObject("article", article);
        modelAndView.addObject("views", zSetOperations.score(REDIS_ARTICLE_VIEWS, id));
        return modelAndView;
    }
}
