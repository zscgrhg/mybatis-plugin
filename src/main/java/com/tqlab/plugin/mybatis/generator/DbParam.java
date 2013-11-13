/**
 * 
 */
package com.tqlab.plugin.mybatis.generator;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

/**
 * @author John Lee
 *
 */
public class DbParam {

    private String                 objectName;
    private FullyQualifiedJavaType type;

    /**
     * @return the objectName
     */
    public final String getObjectName() {
        return objectName;
    }

    /**
     * @param objectName the objectName to set
     */
    public final void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * @return the type
     */
    public final FullyQualifiedJavaType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public final void setType(FullyQualifiedJavaType type) {
        this.type = type;
    }
}
