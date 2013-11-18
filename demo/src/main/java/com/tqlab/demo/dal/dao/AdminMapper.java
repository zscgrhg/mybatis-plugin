package com.tqlab.demo.dal.dao;

import com.tqlab.demo.dal.dataobject.Admin;
import com.tqlab.demo.dal.dataobject.AdminKey;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;

public interface AdminMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ADMIN
     *
     * @mbggenerated Mon Jul 16 18:38:33 CST 2012
     */
    @Delete({
        "delete from ADMIN",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(AdminKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ADMIN
     *
     * @mbggenerated Mon Jul 16 18:38:33 CST 2012
     */
    @Insert({
        "insert into ADMIN (ID, NAME, ",
        "PWD)",
        "values (#{id,jdbcType=INTEGER}, #{name,jdbcType=VARCHAR}, ",
        "#{pwd,jdbcType=VARCHAR})"
    })
    int insert(Admin record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ADMIN
     *
     * @mbggenerated Mon Jul 16 18:38:33 CST 2012
     */
    @InsertProvider(type=AdminSqlProvider.class, method="insertSelective")
    int insertSelective(Admin record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ADMIN
     *
     * @mbggenerated Mon Jul 16 18:38:33 CST 2012
     */
    @Select({
        "select",
        "ID, NAME, PWD",
        "from ADMIN",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    @Results({
        @Result(column="ID", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="NAME", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="PWD", property="pwd", jdbcType=JdbcType.VARCHAR)
    })
    Admin selectByPrimaryKey(AdminKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ADMIN
     *
     * @mbggenerated Mon Jul 16 18:38:33 CST 2012
     */
    @UpdateProvider(type=AdminSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(Admin record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ADMIN
     *
     * @mbggenerated Mon Jul 16 18:38:33 CST 2012
     */
    @Update({
        "update ADMIN",
        "set NAME = #{name,jdbcType=VARCHAR},",
          "PWD = #{pwd,jdbcType=VARCHAR}",
        "where ID = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(Admin record);
}