package com.citic.asp.test.loader;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.engine.event.LoopIterationListener;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 加载配置文件，从配置文件中读取账号密码，然后将账号密码信息设置到jmeter的变量中
 *
 * @author qcb
 * @date 2021/04/19 16:17.
 */
public class LoadGenerator extends ConfigTestElement implements TestBean, LoopIterationListener {

    private static final Logger log = LoggingManager.getLoggerForClass();

    /**
     * 发送用户
     */
    private String fromUser;

    /**
     * 接受用户
     */
    private String toUser;

    /**
     * 群
     */
    private String group;

    /**
     * 发送人用户账号变量名
     */
    private String fromUsernameVariableName;

    /**
     * 发送人密码变量名
     */
    private String fromUserPasswordVariableName;

    /**
     * 发送设备变量名
     */
    private String fromDeviceVariableName;
    /**
     * 发送设备类型变量名
     */
    private String fromDeviceTypeVariableName;

    /**
     * 接收人账号变量名
     */
    private String toUsernameVariableName;
    /**
     * 接收设备变量名
     */
    private String toDeviceVariableName;
    /**
     * 接收设备类型变量名
     */
    private String toDeviceTypeVariableName;

    /**
     * 群ID变量名
     */
    private String groupIdVariableName;

    /**
     * 群名称变量名
     */
    private String groupNameVariableName;

    private static volatile SyntheticLoadGenerator<Account> fromUserGenerator;

    private static volatile SyntheticLoadGenerator<Account> toUserGenerator;

    private static volatile SyntheticLoadGenerator<Group> groupGenerator;

    private static final Object lock = new Object();

    private void init(){
        if(fromUserGenerator == null || toUserGenerator == null || groupGenerator == null){
            synchronized (lock){
                if(fromUserGenerator == null){
                    fromUserGenerator = new AccountLoadGenerator(loadAccounts(getFromUser()));
                }
                if(toUserGenerator == null){
                    toUserGenerator = new AccountLoadGenerator(loadAccounts(getToUser()));
                }
                if(groupGenerator == null){
                    groupGenerator = new GroupLoadGenerator(loadGroups(getGroup()));
                }
            }
        }
    }

    @Override
    public void iterationStart(LoopIterationEvent loopIterationEvent) {
        init();
        Account fromUser = fromUserGenerator.next();
        JMeterVariables variables = JMeterContextService.getContext().getVariables();
        variables.put(getFromUsernameVariableName(), fromUser.getUsername());
        variables.put(getFromUserPasswordVariableName(), fromUser.getPassword());
        variables.put(getFromDeviceVariableName(), fromUser.getDeviceId());
        variables.put(getFromDeviceTypeVariableName(), fromUser.getDeviceType());
        log.info("fromUser:" + fromUser);

        Account toUser = toUserGenerator.next();
        variables.put(getToUsernameVariableName(), toUser.getUsername());
        variables.put(getToDeviceVariableName(), toUser.getDeviceId());
        variables.put(getToDeviceTypeVariableName(), toUser.getDeviceType());
        log.info("toUser:" + toUser);

        Group group = groupGenerator.next();
        variables.put(getGroupIdVariableName(), group.getGroupId());
        variables.put(getGroupNameVariableName(), group.getGroupName());
        log.info("group:" + group);
    }

    public <T> List<T> load(String filename, Function<String, T> function){
        List<T> list = new ArrayList<>();
        if(StringUtils.isEmpty(filename)){
            return list;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            String line;
            while((line = reader.readLine()) != null) {
                list.add(function.apply(line));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    /**
     * 从配置文件加载群信息
     * @param fileName
     * @return
     */
    public List<Group> loadGroups(String fileName){
        return load(fileName, s -> {
            String[] arr = s.split(",");
            if(arr.length > 0){
                String groupId = arr[0].trim();
                String groupName = arr.length > 1 ? arr[1].trim() : "";
                return new Group(groupId, groupName);
            }
            return new Group("", "");
        });
    }

    /**
     * 从配置文件中加载账号信息
     * @return
     */
    public List<Account> loadAccounts(String fileName){
        return load(fileName, s -> {
            String[] arr = s.split(",");
            if(arr.length > 0){
                String username = arr[0].trim();
                String password = arr.length > 1 ? arr[1].trim() : "";
                String deviceId = arr.length > 2 ? arr[2].trim() : "";
                String deviceType = arr.length > 3 ? arr[3].trim() : "phone";
                return new Account(username, password, deviceId, deviceType);
            }
            return new Account("", "", "");
        });
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getFromUsernameVariableName() {
        return fromUsernameVariableName;
    }

    public void setFromUsernameVariableName(String fromUsernameVariableName) {
        this.fromUsernameVariableName = fromUsernameVariableName;
    }

    public String getFromUserPasswordVariableName() {
        return fromUserPasswordVariableName;
    }

    public void setFromUserPasswordVariableName(String fromUserPasswordVariableName) {
        this.fromUserPasswordVariableName = fromUserPasswordVariableName;
    }

    public String getToUsernameVariableName() {
        return toUsernameVariableName;
    }

    public void setToUsernameVariableName(String toUsernameVariableName) {
        this.toUsernameVariableName = toUsernameVariableName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroupIdVariableName() {
        return groupIdVariableName;
    }

    public void setGroupIdVariableName(String groupIdVariableName) {
        this.groupIdVariableName = groupIdVariableName;
    }

    public String getGroupNameVariableName() {
        return groupNameVariableName;
    }

    public void setGroupNameVariableName(String groupNameVariableName) {
        this.groupNameVariableName = groupNameVariableName;
    }

    public String getFromDeviceVariableName() {
        return fromDeviceVariableName;
    }

    public void setFromDeviceVariableName(String fromDeviceVariableName) {
        this.fromDeviceVariableName = fromDeviceVariableName;
    }

    public String getToDeviceVariableName() {
        return toDeviceVariableName;
    }

    public void setToDeviceVariableName(String toDeviceVariableName) {
        this.toDeviceVariableName = toDeviceVariableName;
    }

    public String getFromDeviceTypeVariableName() {
        return fromDeviceTypeVariableName;
    }

    public void setFromDeviceTypeVariableName(String fromDeviceTypeVariableName) {
        this.fromDeviceTypeVariableName = fromDeviceTypeVariableName;
    }

    public String getToDeviceTypeVariableName() {
        return toDeviceTypeVariableName;
    }

    public void setToDeviceTypeVariableName(String toDeviceTypeVariableName) {
        this.toDeviceTypeVariableName = toDeviceTypeVariableName;
    }
}