package top.lvsongsong.blog.service;

import top.lvsongsong.blog.modal.Article;

/**
 * list,get,count,save,update,remove
 *
 * @author LvSS
 * @create 2018/05/16 17:08
 */
public interface ArticleService {

    /**
     * 根据主键查询文章
     *
     * @param articleID
     * @return
     */
    Article findOneByID(String articleID);

    void saveArticle(Article article);
}
