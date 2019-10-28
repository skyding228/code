package ${package.ServiceImpl};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
<#assign dao="${table.mapperName?uncap_first}">
<#list table.fields as field>
 <#if field.keyFlag>
  <#assign klass="${field.columnType.pkg!'0'}" klassName="${field.columnType.type}" propName="${field.name}">
 </#if>
</#list>
/**
 * <p>
 * ${table.comment!} 服务实现类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
public class ${table.serviceImplName} implements ${table.serviceName} {

    @Autowired
    private ${table.mapperName} ${dao};

    @Override
    public int deleteById(${klassName} ${propName}){
        return ${dao}.deleteById(${propName});
    }

    @Override
    public int insert(${entity} ${entity?uncap_first}){
        return ${dao}.insert( ${entity?uncap_first});
    }

    @Override
    public ${entity} selectById(${klassName} ${propName}){
        return ${dao}.selectById(${propName});
    }

    @Override
    public int updateById(${entity} ${entity?uncap_first}){
        return ${dao}.updateById(${entity?uncap_first});
    }
}
