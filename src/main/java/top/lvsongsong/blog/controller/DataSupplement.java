package top.lvsongsong.blog.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO 补充数据
 *
 * @author LvSS
 * @create 2018/07/23 10:56
 */
@RestController
@RequestMapping("/supplement")
public class DataSupplement {

    /**
     * TODO 添加文章到指定分类
     */
    @RequestMapping
    public void addArticle2Classify(String classify, String articleID) {

    }
}
