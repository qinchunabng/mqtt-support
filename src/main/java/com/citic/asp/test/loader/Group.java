package com.citic.asp.test.loader;

import java.io.Serializable;

/**
 * 群信息
 *
 * @author qcb
 * @date 2021/04/23 10:32.
 */
public class Group implements Serializable {
    private static final long serialVersionUID = 4032584199991579034L;

    public Group(){}

    public Group(String groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
    }

    /**
     * 群ID
     */
    private String groupId;

    /**
     * 群名称
     */
    private String groupName;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                '}';
    }
}
