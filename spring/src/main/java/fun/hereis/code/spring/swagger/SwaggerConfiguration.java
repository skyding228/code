package fun.hereis.code.spring.swagger;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * swagger 配置类
 *
 * @author weichunhe
 */
@Configuration
@ComponentScan(basePackageClasses = SwaggerInfo.class)
@EnableSwagger2
public class SwaggerConfiguration {
    @Autowired
    private SwaggerInfo swaggerInfo;

    @Bean
    public Docket controllerApi() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName(swaggerInfo.getGroupName())
                .apiInfo(apiInfo());
        if (!StringUtils.isEmpty(swaggerInfo.getHost())) {
            docket.host(swaggerInfo.getHost());
        }
        ApiSelectorBuilder builder = docket.select();
        if (!StringUtils.isEmpty(swaggerInfo.getBasePackage())) {
            builder = builder.apis(RequestHandlerSelectors.basePackage(swaggerInfo.getBasePackage()));
        }
        if (!StringUtils.isEmpty(swaggerInfo.getRegexPath())) {
            String[] paths = swaggerInfo.getRegexPath().split(",");
            Predicate[] predicates = new Predicate[paths.length];
            for (int i = 0; i < paths.length; i++) {
                predicates[i] = PathSelectors.regex(paths[i]);
            }
            builder = builder.paths(Predicates.or(predicates));
        }


        return builder.build();
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(swaggerInfo.getTitle())
                .description(swaggerInfo.getDescription())
                .termsOfServiceUrl("http://springfox.io")
//                .contact(new Contact("skyding"))
                .license(swaggerInfo.getLicense())
                .licenseUrl("https://github.com/springfox/springfox/blob/master/LICENSE")
                .version("2.0")
                .build();
    }
}
