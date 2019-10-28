package ${package.Mapper};
<#list table.fields as field>
 <#if field.keyFlag>
  <#assign klass="${field.columnType.pkg!'0'}" klassName="${field.columnType.type}" propName="${field.name}">
 </#if>
</#list>
import ${package.Entity}.${entity};
<#if  klass != "0">
import ${klass};
</#if>

/**
 * <p>
 * ${table.comment!} Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
public interface ${table.mapperName} {

    /**
     * delete by id
     * @param ${propName} row id
     * @return affect row number
     */
    int deleteById(${klassName} ${propName});

    /**
     * insert
     * @param ${entity?uncap_first} data
     * @return affect row number
     */
    int insert(${entity} ${entity?uncap_first});

    /**
     * select
     * @param ${propName} row id
     * @return data
     */
    ${entity} selectById(${klassName} ${propName});


    /**
     * update
     * @param ${entity?uncap_first} data
     * @return affect row number
     */
    int updateById(${entity} ${entity?uncap_first});
}

