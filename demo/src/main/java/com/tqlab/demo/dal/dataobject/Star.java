package com.tqlab.demo.dal.dataobject;

public class Star extends StarKey {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column STAR.FIRSTNAME
     *
     * @mbggenerated Mon Jul 16 18:38:33 CST 2012
     */
    private String firstname;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column STAR.LASTNAME
     *
     * @mbggenerated Mon Jul 16 18:38:33 CST 2012
     */
    private String lastname;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column STAR.FIRSTNAME
     *
     * @return the value of STAR.FIRSTNAME
     *
     * @mbggenerated Mon Jul 16 18:38:33 CST 2012
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column STAR.FIRSTNAME
     *
     * @param firstname the value for STAR.FIRSTNAME
     *
     * @mbggenerated Mon Jul 16 18:38:33 CST 2012
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname == null ? null : firstname.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column STAR.LASTNAME
     *
     * @return the value of STAR.LASTNAME
     *
     * @mbggenerated Mon Jul 16 18:38:33 CST 2012
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column STAR.LASTNAME
     *
     * @param lastname the value for STAR.LASTNAME
     *
     * @mbggenerated Mon Jul 16 18:38:33 CST 2012
     */
    public void setLastname(String lastname) {
        this.lastname = lastname == null ? null : lastname.trim();
    }
}