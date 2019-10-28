package ${package.Service};

import ${package.Entity}.${entity};
<#list table.fields as field>
 <#if field.keyFlag>
  <#assign klass="${field.columnType.pkg!'0'}" klassName="${field.columnType.type}" propName="${field.name}">
 </#if>
</#list>
<#if  klass != "0">
import ${klass};
</#if>
/**
 * <p>
 * ${table.comment!} 服务类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
public interface ${table.serviceName}  {

    int deleteById(${klassName} ${propName});

    int insert(${entity} ${entity?uncap_first});

    ${entity} selectById(${klassName} ${propName});

    int updateById(${entity} ${entity?uncap_first});
}
