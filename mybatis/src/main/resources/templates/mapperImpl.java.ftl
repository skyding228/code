package ${package.Mapper}.impl;
<#list table.fields as field>
 <#if field.keyFlag>
  <#assign klass="${field.columnType.pkg!'0'}" klassName="${field.columnType.type}" propName="${field.name}">
 </#if>
</#list>
import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

<#if  klass != "0">
import ${klass};
</#if>

<#assign impl="${table.mapperName}Impl" dao="${table.mapperName?uncap_first}">
/**
 * <p>
 * ${table.comment!} Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
public class ${impl} implements ${table.mapperName}{

    private ${table.mapperName} ${dao};

    @Autowired
    public ${impl}(SqlSession sqlSession){
        ${dao} = sqlSession.getMapper(${table.mapperName}.class);
    }

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

