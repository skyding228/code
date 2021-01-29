package fun.hereis.code.spring.hotrefresh;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * 热加载参数
 * @author weichunhe
 * created at 2021/1/28
 */
@ApiModel
public class HotLoadParam {

    @ApiModelProperty("完整类路径名称")
    private String classFullName;

    @ApiModelProperty("容器中的beanName")
    private String beanName;

    @ApiModelProperty("同一个版本只执行一次")
    private int version;


    public String getClassFullName() {
        return classFullName;
    }

    public void setClassFullName(String classFullName) {
        this.classFullName = classFullName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
