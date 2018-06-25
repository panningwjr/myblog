package top.lvsongsong.blog.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author LvSS
 * @create 2018/04/27 18:18
 */
public class ArticleVO implements Serializable {

    private static final long serialVersionUID = -6233530814940225009L;

    @Override
    public String toString() {
        return "ArticleVO{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", classification='" + classification + '\'' +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", icon='" + icon + '\'' +
                '}';
    }

    private String id;
    private String title;
    private String classification;
    private String content;
    private Date createTime;
    private Date modifyTime;
    private String icon;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

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
