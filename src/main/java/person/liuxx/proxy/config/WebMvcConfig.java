package person.liuxx.proxy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年7月26日 下午4:38:54
 * @since 1.0.0
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter
{
    @Override
    public void addViewControllers(ViewControllerRegistry registry)
    {
        // registry.addViewController("/login").setViewName("login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        // registry.addResourceHandler("/static/views/**").addResourceLocations(
        // "classpath:/static/views/");
    }
}
