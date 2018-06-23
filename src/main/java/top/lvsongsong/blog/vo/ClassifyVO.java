package top.lvsongsong.blog.vo;

import java.io.Serializable;

/**
 * @author LvSS
 * @create 2018/06/15 13:28
 */
public class ClassifyVO implements Serializable {


    private static final long serialVersionUID = -2460580430579455717L;
    private String name;

    private String code;

    private String icon;

    @Override
    public int hashCode() {
        return name.hashCode() * 13 + code.hashCode() * 19 + icon.hashCode() * 31;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj != null && obj.getClass() == ClassifyVO.class) {
            ClassifyVO classifyVO = (ClassifyVO) obj;
            if (classifyVO.getName().equals(this.getName()) && classifyVO.getCode().equals(this.getCode()) && classifyVO.getIcon().equals(this.getIcon())) {
                return true;
            }
        }
        return super.equals(obj);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
