package top.lvsongsong.blog.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author LvSS
 * @create 2018/05/09 18:09
 */
@RestController
@RequestMapping("/views")
public class ViewController {

    @RequestMapping("/{path1}")
    public ModelAndView articles(@PathVariable("path1") String path1) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(path1);
        return modelAndView;
    }

    @RequestMapping("/{path1}/{path2}")
    public ModelAndView articles(@PathVariable("path1") String path1, @PathVariable("path2") String path2) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(path1 + "/" + path2);
        return modelAndView;
    }

    @RequestMapping("/{path1}/{path2}/{path3}")
    public ModelAndView articles(@PathVariable("path1") String path1, @PathVariable("path2") String path2, @PathVariable("path3") String path3) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(path1 + "/" + path2 + "/" + path3);
        return modelAndView;
    }
}
