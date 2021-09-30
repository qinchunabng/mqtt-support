package com.citic.asp.test.loader;

import java.util.List;
import java.util.Random;

/**
 * description
 *
 * @author DELL
 * @date 2021/04/23 10:44.
 */
public class GroupLoadGenerator implements SyntheticLoadGenerator<Group>{

    private List<Group> groupList;

    public GroupLoadGenerator(List<Group> groupList) {
        this.groupList = groupList;
    }

    @Override
    public Group next() {
        if(groupList.isEmpty()){
            return new Group();
        }
        Random random = new Random();
        int index = random.nextInt(groupList.size());
        return groupList.get(index);
    }
}
