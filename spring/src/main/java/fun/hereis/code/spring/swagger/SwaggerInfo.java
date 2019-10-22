package fun.hereis.code.spring.swagger;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * swagger 配置信息
 */
@Component
@ConfigurationProperties(prefix = "swagger")
public class SwaggerInfo {
    /**
     * 分组名称
     */
    private String groupName = "controller";

    /**
     * 需要暴露API的controller的基础包,例如 org.wch
     */
    private String basePackage;

    /**
     * 根据路径进行配置暴露路径,正则表达式,多个之间用,分隔, 例如/user.*,/menu.*
     */
    private String regexPath;

    /**
     * 文档名称
     */
    private String title = "HTTP API";

    /**
     * 文档描述
     */
    private String description = "Swagger 自动生成接口文档";

    /**
     * license 信息
     */
    private String license = "Apache License Version 2.0";

    /**
     * 指定host文档上调用接口的地址,例如localhost:8080
     */
    private String host;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getRegexPath() {
        return regexPath;
    }

    public void setRegexPath(String regexPath) {
        this.regexPath = regexPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
