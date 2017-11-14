package person.liuxx.proxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年7月31日 下午3:02:34
 * @since 1.0.0
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig
{
    @Bean
    public Docket createRestApi()
    {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.hyc.it.project.plan.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo()
    {
        return new ApiInfoBuilder().title("Project Plan Service APIs")
                .description("Project Plan Service ：http://localhost:8080/")
                .termsOfServiceUrl("http://blog.didispace.com/")
                .contact(new Contact("aa", "aa.aa.com", "aa@aa.com"))
                .version("1.0")
                .build();
    }
}
