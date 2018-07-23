package top.lvsongsong.blog;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringRunner;
import top.lvsongsong.blog.constant.RedisConstants;
import top.lvsongsong.blog.vo.ArticleVO;

import java.util.Calendar;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlogApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void contextLoads() {
    }

    @Test
    public void classifyArticle() {
        redisTemplate.opsForList().rightPush(RedisConstants.REDIS_ARTILES + ":javase", "e0a6e0c5b9e24bbd868eab924f3f063c");
    }

    @Test
    public void initTestData() {
        for (int i = 4; i < 8; i++) {
            Calendar current = Calendar.getInstance();
            current.set(Calendar.MONTH, 4);
            current.set(Calendar.YEAR, 2018);
            ArticleVO articleVO = new ArticleVO();
            articleVO.setId("jvm" + i);
            articleVO.setTitle("test" + i);
            articleVO.setClassification("jvm");
            articleVO.setCreateTime(current.getTime());
            articleVO.setModifyTime(current.getTime());
            //记录文章的发布时间
            ZSetOperations<String, String> zSetOperations = (ZSetOperations<String, String>) this.redisTemplate.opsForZSet();
            zSetOperations.add("time", articleVO.getId(), current.getTime().getTime());
            //向redis 中添加文章记录hash articles: id-article
            this.redisTemplate.opsForHash().put("articles", articleVO.getId(), articleVO);
            //添加文章分类队列 list  articles:jvm id
            ListOperations<String, String> listOperations = (ListOperations<String, String>) this.redisTemplate.opsForList();
            listOperations.rightPush("articles" + ":" + articleVO.getClassification(), articleVO.getId());
        }
    }

}
