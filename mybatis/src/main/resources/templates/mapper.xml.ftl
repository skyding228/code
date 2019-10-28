<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${package.Mapper}.${table.mapperName}">

<#if enableCache>
    <!-- 开启二级缓存 -->
    <cache type="org.mybatis.caches.ehcache.LoggingEhcache"/>

</#if>
<#if baseResultMap>
    <!-- 通用查询映射结果 -->
    <resultMap id="BaseResultMap" type="${package.Entity}.${entity}">
        <#list table.fields as field>
            <#if field.keyFlag><#--生成主键排在第一位-->
        <id column="${field.name}" property="${field.propertyName}"/>
            </#if>
        </#list>
<#list table.commonFields as field><#--生成公共字段 -->
    <result column="${field.name}" property="${field.propertyName}"/>
</#list>
<#list table.fields as field>
    <#if !field.keyFlag><#--生成普通字段 -->
        <result column="${field.name}" property="${field.propertyName}"/>
    </#if>
</#list>
    </resultMap>

</#if>
<#if baseColumnList>
    <!-- 通用查询结果列 -->
    <sql id="Base_Column_List">
        <#list table.commonFields as field>
            ${field.name},
        </#list>
        ${table.fieldNames}
    </sql>
</#if>

<#list table.fields as field>
    <#if field.keyFlag>
    <#assign type="${field.columnType.pkg!('java.lang.'+field.columnType.type)}">
    <select id="selectById" parameterType="${type}" resultMap="BaseResultMap">
        select <include refid="Base_Column_List"></include>
        from ${table.name} where ${field.name} = ${r"#{"}${field.propertyName}}
    </select>
    <delete id="deleteById" parameterType="${type}">
        delete from ${table.name} where ${field.name} =${r"#{"}${field.propertyName}}
    </delete>

        <#assign columnArray=[],valueArray=[]>
        <#list table.fields as field>
            <#assign columnArray=columnArray+["${field.name}"],valueArray=valueArray+["${r'#{'}"+"${field.propertyName}}"]>
        </#list>
    <insert id="insert" parameterType="${package.Entity}.${entity}">
        insert into ${table.name} (${columnArray?join(', ')})
        values (${valueArray?join(', ')})
    </insert>
     <update id="updateById" parameterType="${package.Entity}.${entity}">
         update ${table.name} set
         <#assign first=true>
 <#list table.fields as field>
     <#if !field.keyFlag>
         <#if !first>,</#if>${field.name} = ${r"#{"}${field.propertyName}}
         <#assign first=false>
     </#if>
 </#list>
 <#list table.fields as field>
     <#if field.keyFlag>
          where ${field.name} = ${r"#{"}${field.propertyName}}
     </#if>
 </#list>
     </update>
    </#if>
</#list>

</mapper>
