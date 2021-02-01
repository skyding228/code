package fun.hereis.code.spring.hotrefresh;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

/**
 * 热加载http端点
 * @author weichunhe
 * created at 2021/1/28
 */
@RestController
@RequestMapping("/_hotLoad")
public class HotLoadController {

    @Autowired
    private WebApplicationContext applicationContext;

    private int version = 0;

    /**
     * 热加载路径
     * @param param 热加载参数
     * @return
     */
    @GetMapping("/load")
    public String load(HotLoadParam param){
        Class targetClass = null;
        try {
            targetClass = Class.forName(param.getClassFullName());
        } catch (ClassNotFoundException e) {
            return "未找到类"+param.getClassFullName();
        }
        if(version == param.getVersion()){
            return "已执行，本次跳过!";
        }
        version = param.getVersion();
        if(StringUtils.isNotEmpty(param.getBaseUrl())){
            HotClassLoader.setBaseUrl(param.getBaseUrl());
        }
        HotClassLoader.reload(targetClass, (ConfigurableApplicationContext) applicationContext,param.getBeanName());
        return "success";
    }

}
