package fun.hereis.code.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码自动生成器
 * created at 2018/8/21
 * @author  weichunhe
 */
public class MyGenerator {

    public static void main(String[] args) {
        String projectDir = "E:\\intelljws\\code\\mybatis";

        String basePackage = "fun.hereis.code.mybatis";


        AutoGenerator mpg = new AutoGenerator();

        mpg.setTemplateEngine(new FreemarkerTemplateEngine());

        // 设置为null，禁止生成
        TemplateConfig tc = new TemplateConfig();
        tc.setXml(null);
        tc.setController(null);
        tc.setService(null);
        tc.setServiceImpl(null);
        mpg.setTemplate(tc);

        // add custom behavior
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<String, Object>();
                this.setMap(map);
            }
        };
        List focList = new ArrayList();
        focList.add(new FileOutConfig("/templates/mapper.xml.ftl") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return projectDir+"/src/main/resources/mapper/" + tableInfo.getEntityName() + ".xml";
            }
        });

        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 全局配置
        final GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir(projectDir+"/src/main/java");
        gc.setFileOverride(true);
        gc.setActiveRecord(false);// 不需要ActiveRecord特性的请改为false
        gc.setEnableCache(false);// XML 二级缓存
        gc.setBaseResultMap(true);// XML ResultMap
        gc.setBaseColumnList(true);// XML columList
        gc.setDateType(DateType.ONLY_DATE);
        gc.setOpen(false);
        gc.setSwagger2(true);
        gc.setAuthor("weichunhe");

        gc.setMapperName("%sDao");
        gc.setServiceName("%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setControllerName("%sController");
        mpg.setGlobalConfig(gc);

        //datasource configuration
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername("sms_qas");
        dsc.setPassword("JLA-89NK");
        dsc.setUrl("jdbc:mysql://localhost:3306/sms_log?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true");
        mpg.setDataSource(dsc);

        StrategyConfig strategy = new StrategyConfig();
        //the controller、service、mapper names will remove the prefix
        strategy.setTablePrefix("");
        strategy.setNaming(NamingStrategy.underline_to_camel);// 表名生成策略

        //TODO
        strategy.setInclude("ali_alarm_morse"
//                , "tb_app_customer_base","tb_app_customer_base_detail","tb_app_customer_base_menu"
        );

        strategy.setRestControllerStyle(true);

        mpg.setStrategy(strategy);

        // package configuration
        PackageConfig pc = new PackageConfig();
        pc.setParent(basePackage);
        pc.setEntity("bean");
        pc.setMapper("dao");
        pc.setService("service");
        pc.setServiceImpl("service.impl");
        pc.setXml(null);

        mpg.setPackageInfo(pc);

        mpg.execute();

    }

}
