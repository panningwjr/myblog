package top.lvsongsong.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.lvsongsong.blog.constant.RedisConstants;
import top.lvsongsong.blog.vo.ClassifyVO;

import java.util.List;

/**
 * @author LvSS
 * @create 2018/06/28 14:10
 */
@RestController
@RequestMapping("/articleclassifies")
public class ArticleClassifyController {

    @Autowired
    private RedisTemplate<String, ? extends Object> redisTemplate;

    /**
     * 删除分类
     *
     * @param index
     * @return
     */
    @DeleteMapping("/{index}")
    public ResponseEntity deleteClassifications(@PathVariable("index") Long index) {
        if (index > 0) {
            ListOperations<String, ClassifyVO> operations = (ListOperations<String, ClassifyVO>) this.redisTemplate.opsForList();
            ClassifyVO classifyVO = operations.index(RedisConstants.REDIS_ARTICLE_CLASSIFICATIONS, index);
            operations.remove(RedisConstants.REDIS_ARTICLE_CLASSIFICATIONS, index, classifyVO);
            return new ResponseEntity(HttpStatus.CREATED);
        } else {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }

    /**
     * 修改分类
     *
     * @param classifyVO
     * @param index
     * @return
     */
    @PutMapping(value = "/{index}", consumes = "application/json")
    public ResponseEntity<ClassifyVO> modifyClassifications(@RequestBody ClassifyVO classifyVO, @PathVariable("index") Long index) {
        ListOperations<String, ClassifyVO> operations = (ListOperations<String, ClassifyVO>) this.redisTemplate.opsForList();
        if (index < 0) {
            operations.rightPush(RedisConstants.REDIS_ARTICLE_CLASSIFICATIONS, classifyVO);
        } else {
            operations.set(RedisConstants.REDIS_ARTICLE_CLASSIFICATIONS, index, classifyVO);
        }
        return new ResponseEntity<ClassifyVO>(classifyVO, HttpStatus.CREATED);
    }

    /**
     * 获取文章分类
     *
     * @return
     */
    @GetMapping(produces = "application/json")
    public List<ClassifyVO> getClassifications() {
        ListOperations<String, ClassifyVO> operations = (ListOperations<String, ClassifyVO>) this.redisTemplate.opsForList();
        Long len = operations.size(RedisConstants.REDIS_ARTICLE_CLASSIFICATIONS);
        List<ClassifyVO> clas = operations.range(RedisConstants.REDIS_ARTICLE_CLASSIFICATIONS, 0, len);
        return clas;
    }

    /**
     * 添加文章分类
     *
     * @param classify
     * @return
     */
    @PostMapping(consumes = "application/json")
    public ResponseEntity<ClassifyVO> addArticleClassify(@RequestBody ClassifyVO classify) {
        ListOperations<String, ClassifyVO> listOperations = (ListOperations<String, ClassifyVO>) this.redisTemplate.opsForList();
        listOperations.rightPush(RedisConstants.REDIS_ARTICLE_CLASSIFICATIONS, classify);
        return new ResponseEntity<ClassifyVO>(classify, HttpStatus.CREATED);
    }
}
