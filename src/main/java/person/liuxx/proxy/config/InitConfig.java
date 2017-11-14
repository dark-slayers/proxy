package person.liuxx.proxy.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;

/**
 * 初始化配置
 * 
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年9月29日 下午2:57:49
 * @since 1.0.0
 */
@Configuration
public class InitConfig
{
    @Bean
    public ServletRegistrationBean servletRegistrationBean()
    {
        // Druid数据源配置
        return new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean()
    {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        // Druid数据源配置
        registrationBean.setFilter(new WebStatFilter());
        return registrationBean;
    }
}
