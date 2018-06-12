package top.lvsongsong.blog.vo;

import java.io.Serializable;

/**
 * @author LvSS
 * @create 2018/04/27 18:18
 */
public class ArticleVO implements Serializable {

    @Override
    public String toString() {
        return "ArticleVO{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", classification='" + classification + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    private String id;
    private String title;
    private String classification;
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
