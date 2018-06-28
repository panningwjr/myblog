package top.lvsongsong.blog.filter;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;

/**
 * @author LvSS
 * @create 2018/05/10 15:45
 */
public class SiteMeshFilter extends ConfigurableSiteMeshFilter {
    @Override
    protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
        builder.addDecoratorPath("/views/*", "/views/layout/default.html").addExcludedPath("/views/layout/default.html").addDecoratorPath("/article/html/*", "/views/layout/default.html").addDecoratorPath("/article/editview/*", "/views/layout/default.html");
    }
}
