package com.tqlab.demo.dal.dataobject;

import java.io.Serializable;

public class StarKey implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column STAR.ID
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table STAR
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column STAR.ID
     *
     * @return the value of STAR.ID
     *
     * @mbggenerated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column STAR.ID
     *
     * @param id the value for STAR.ID
     *
     * @mbggenerated
     */
    public void setId(Integer id) {
        this.id = id;
    }
}