package com.tqlab.demo.dal.dataobject;

public class Movies extends MoviesKey {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column MOVIES.STARID
     *
     * @mbggenerated Mon Jul 16 18:38:33 CST 2012
     */
    private Integer starid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column MOVIES.TITLE
     *
     * @mbggenerated Mon Jul 16 18:38:33 CST 2012
     */
    private String title;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column MOVIES.STARID
     *
     * @return the value of MOVIES.STARID
     *
     * @mbggenerated Mon Jul 16 18:38:33 CST 2012
     */
    public Integer getStarid() {
        return starid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column MOVIES.STARID
     *
     * @param starid the value for MOVIES.STARID
     *
     * @mbggenerated Mon Jul 16 18:38:33 CST 2012
     */
    public void setStarid(Integer starid) {
        this.starid = starid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column MOVIES.TITLE
     *
     * @return the value of MOVIES.TITLE
     *
     * @mbggenerated Mon Jul 16 18:38:33 CST 2012
     */
    public String getTitle() {
        return title;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column MOVIES.TITLE
     *
     * @param title the value for MOVIES.TITLE
     *
     * @mbggenerated Mon Jul 16 18:38:33 CST 2012
     */
    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }
}