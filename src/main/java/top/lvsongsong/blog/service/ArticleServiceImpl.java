package top.lvsongsong.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.lvsongsong.blog.mapper.ArticleMapper;
import top.lvsongsong.blog.modal.Article;

/**
 * @author LvSS
 * @create 2018/05/16 17:09
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public Article findOneByID(String articleId) {
        return this.articleMapper.selectByPrimaryKey(articleId);
    }

    @Override
    public void saveArticle(Article article) {
        this.articleMapper.insertSelective(article);
    }
}
