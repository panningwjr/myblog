package top.lvsongsong.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import top.lvsongsong.blog.constant.RedisConstants;
import top.lvsongsong.blog.mapper.ArticleMapper;
import top.lvsongsong.blog.modal.Article;
import top.lvsongsong.blog.vo.ArticleVO;

import java.util.Date;

/**
 * @author LvSS
 * @create 2018/05/16 17:09
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private RedisTemplate<String, ? extends Object> redisTemplate;

    @Override
    public Article findOneByID(String articleId) {
        return this.articleMapper.selectByPrimaryKey(articleId);
    }

    @Override
    public void saveArticle(Article article) {
        ArticleVO articleVO = new ArticleVO();
        articleVO.setId(article.getArticleId());
        articleVO.setTitle(article.getTitle());
        articleVO.setClassification(article.getClassify());
        articleVO.setCreateTime(article.getCreatetime());
        articleVO.setModifyTime(article.getModifytime());
        //记录文章的发布时间
        ZSetOperations<String, String> zSetOperations = (ZSetOperations<String, String>) this.redisTemplate.opsForZSet();
        zSetOperations.add(RedisConstants.REDIS_ARTICLE_TIME, articleVO.getId(), articleVO.getCreateTime().getTime());
        //向redis 中添加文章记录hash articles: id-article
        this.redisTemplate.opsForHash().put(RedisConstants.REDIS_ARTILES, articleVO.getId(), articleVO);
        //添加文章分类队列 list  articles:jvm id
        ListOperations<String, String> listOperations = (ListOperations<String, String>) this.redisTemplate.opsForList();
        listOperations.rightPush(RedisConstants.REDIS_ARTILES + ":" + article.getClassify(), articleVO.getId());
        this.articleMapper.insertSelective(article);
    }

    @Override
    public void updateAtricle(Article article) {
        Date current = new Date();
        article.setModifytime(current);
        ArticleVO articleVO = new ArticleVO();
        articleVO.setId(article.getArticleId());
        articleVO.setTitle(article.getTitle());
        articleVO.setClassification(article.getClassify());
        articleVO.setModifyTime(current);
        articleVO.setCreateTime(article.getCreatetime());
        //向redis 中更新文章记录hash articles: id-article
        this.redisTemplate.opsForHash().put(RedisConstants.REDIS_ARTILES, articleVO.getId(), articleVO);
        this.articleMapper.updateByPrimaryKeySelective(article);
    }
}
