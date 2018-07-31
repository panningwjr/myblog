package top.lvsongsong.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import top.lvsongsong.blog.constant.RedisConstants;
import top.lvsongsong.blog.modal.Article;
import top.lvsongsong.blog.service.ArticleService;
import top.lvsongsong.blog.util.BaseStringUtil;
import top.lvsongsong.blog.vo.ArticleVO;
import top.lvsongsong.blog.vo.ClassifyVO;

import java.net.URI;
import java.util.*;

/**
 * @author LvSS
 * @create 2018/05/11 18:02
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private RedisTemplate<String, ? extends Object> redisTemplate;

    @Autowired
    private ArticleService articleService;

    /**
     * 文章数量及访问量统计
     *
     * @return
     */
    @GetMapping("/statistics")
    public Map<String, Object> accessCounts() {
        ZSetOperations<String, ArticleVO> operations = (ZSetOperations<String, ArticleVO>) this.redisTemplate.opsForZSet();
        Long totalArticles = operations.zCard(RedisConstants.REDIS_ARTICLE_VIEWS);
        Set<ZSetOperations.TypedTuple<ArticleVO>> articleVOS = operations.rangeWithScores(RedisConstants.REDIS_ARTICLE_VIEWS, 0L, totalArticles);
        Double totalCounts = 0.0d;
        Map<String, Object> result = new HashMap<>(4);
        for (ZSetOperations.TypedTuple<ArticleVO> articleWithScore : articleVOS) {
            totalCounts += articleWithScore.getScore();
        }
        result.put("articles", totalArticles);
        result.put("accesscounts", totalCounts);
        return result;
    }

    /**
     * 获取修改文章的页面
     *
     * @param articleid
     * @return
     */
    @GetMapping("/editview/{articleid}")
    public ModelAndView eidtView(@PathVariable String articleid) {
        ModelAndView result = new ModelAndView("/articles/add");
        Article article = this.articleService.findOneByID(articleid);
        result.addObject("article", article);
        return result;
    }

    /**
     * 获取最近的n篇文章
     *
     * @return
     */
    @GetMapping("/recent/{limit}")
    public List<ArticleVO> recentArticles(@PathVariable Integer limit) {
        limit = (limit == null) ? 9 : (limit - 1);
        Set<String> articleids = (Set<String>) this.redisTemplate.opsForZSet().reverseRange(RedisConstants.REDIS_ARTICLE_TIME, 0, limit);
        HashOperations<String, String, ArticleVO> hashOperations = this.redisTemplate.opsForHash();
        List<ArticleVO> articleVOS = hashOperations.multiGet(RedisConstants.REDIS_ARTILES, articleids);
        this.handleArticleIcon(articleVOS);
        return articleVOS;
    }

    /**
     * 根据文章分类设置文章的图标
     *
     * @param articleVOS
     */
    private void handleArticleIcon(List<ArticleVO> articleVOS) {
        ListOperations<String, ClassifyVO> operations = (ListOperations<String, ClassifyVO>) this.redisTemplate.opsForList();
        Long len = operations.size(RedisConstants.REDIS_ARTICLE_CLASSIFICATIONS);
        List<ClassifyVO> classifyVOS = operations.range(RedisConstants.REDIS_ARTICLE_CLASSIFICATIONS, 0, len);
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
     * 获取分类的文章列表
     *
     * @return
     */
    @GetMapping("/classifyarticles")
    public Map<String, List<ArticleVO>> allArticles() {
        HashOperations<String, String, ArticleVO> hashOperations = this.redisTemplate.opsForHash();
        ListOperations<String, ClassifyVO> classifyOperations = (ListOperations<String, ClassifyVO>) this.redisTemplate.opsForList();
        ListOperations<String, String> articleListOperations = (ListOperations<String, String>) this.redisTemplate.opsForList();
        Long size = classifyOperations.size(RedisConstants.REDIS_ARTICLE_CLASSIFICATIONS);
        List<ClassifyVO> classifications = classifyOperations.range(RedisConstants.REDIS_ARTICLE_CLASSIFICATIONS, 0, size);
        Map<String, List<ArticleVO>> articles = new LinkedHashMap<>(size.intValue());
        for (ClassifyVO classifyVO : classifications) {
            Long articleSize = articleListOperations.size(RedisConstants.REDIS_ARTILES + ":" + classifyVO.getCode());
            //获取每个分类队列里的文章
            List<String> articleids = articleListOperations.range(RedisConstants.REDIS_ARTILES + ":" + classifyVO.getCode(), 0, articleSize);
            //根据文章id的list从 hash中一次性取出所有的文章
            List<ArticleVO> articleVOS = hashOperations.multiGet(RedisConstants.REDIS_ARTILES, articleids);
            articles.put(classifyVO.getName() + "," + classifyVO.getIcon(), articleVOS);
        }
        return articles;
    }

    /**
     * 更新文章
     *
     * @param article
     * @return
     */
    @PutMapping(consumes = "application/json")
    public ResponseEntity<Article> updateArticle(@RequestBody Article article) {
        this.articleService.updateAtricle(article);
        HttpHeaders headers = new HttpHeaders();
        URI locationUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/html/{id}").buildAndExpand(article.getArticleId()).toUri();
        headers.setLocation(locationUri);
        ResponseEntity<Article> responseEntity = new ResponseEntity<>(article, headers, HttpStatus.CREATED);
        return responseEntity;
    }

    /**
     * 保存新增文章
     *
     * @param article
     * @return
     */
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        article.setArticleId(BaseStringUtil.getUid());
        Date current = new Date();
        article.setCreatetime(current);
        article.setModifytime(current);
        this.articleService.saveArticle(article);
        HttpHeaders headers = new HttpHeaders();
        URI locationUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/html/{id}").buildAndExpand(article.getArticleId()).toUri();
        headers.setLocation(locationUri);
        ResponseEntity<Article> responseEntity = new ResponseEntity<>(article, headers, HttpStatus.CREATED);
        return responseEntity;
    }

    /**
     * 获取文章html视图
     *
     * @param id
     * @return
     */
    @GetMapping("/html/{id}")
    public ModelAndView selectArticle(@PathVariable String id) {
        ModelAndView modelAndView = new ModelAndView("/articles/article_template");
        //文章访问计数+1
        ZSetOperations<String, String> zSetOperations = (ZSetOperations<String, String>) this.redisTemplate.opsForZSet();
        zSetOperations.incrementScore(RedisConstants.REDIS_ARTICLE_VIEWS, id, 1);
        Article article = this.articleService.findOneByID(id);
        modelAndView.addObject("article", article);
        modelAndView.addObject("views", zSetOperations.score(RedisConstants.REDIS_ARTICLE_VIEWS, id));
        return modelAndView;
    }
}
