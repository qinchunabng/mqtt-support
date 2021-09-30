package com.citic.asp.test.loader;

import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.beans.PropertyDescriptor;

/**
 * description
 *
 * @author qcb
 * @date 2021/04/19 16:46.
 */
public class LoadGeneratorBeanInfo extends BeanInfoSupport {

    private static final Logger log = LoggingManager.getLoggerForClass();

    private static final String FROM_USER = "fromUser";
    private static final String TO_USER = "toUser";
    private static final String FROM_USERNAME_VARIABLE_NAME = "fromUsernameVariableName";
    private static final String FROM_USER_PASSWORD_VARIABLE_NAME  = "fromUserPasswordVariableName";
    private static final String TO_USERNAME_VARIABLE_NAME = "toUsernameVariableName";
    private static final String GROUP = "group";
    private static final String GROUP_ID_VARIABLE_NAME = "groupIdVariableName";
    private static final String GROUP_NAME_VARIABLE_NAME = "groupNameVariableName";
    private static final String FROM_DEVICE_VARIABLE_NAME = "fromDeviceVariableName";
    private static final String FROM_DEVICE_TYPE_VARIABLE_NAME = "fromDeviceTypeVariableName";
    private static final String TO_DEVICE_VARIABLE_NAME = "toDeviceVariableName";
    private static final String TO_DEVICE_TYPE_VARIABLE_NAME = "toDeviceTypeVariableName";

    public LoadGeneratorBeanInfo() {
        super(LoadGenerator.class);
        init();
    }

    private void init(){
        createPropertyGroup("load_generator",new String[]{
                FROM_USER,
                TO_USER,
                FROM_USERNAME_VARIABLE_NAME,
                FROM_USER_PASSWORD_VARIABLE_NAME,
                FROM_DEVICE_VARIABLE_NAME,
                FROM_DEVICE_TYPE_VARIABLE_NAME,
                TO_USERNAME_VARIABLE_NAME,
                TO_DEVICE_VARIABLE_NAME,
                TO_DEVICE_TYPE_VARIABLE_NAME,
                GROUP,
                GROUP_ID_VARIABLE_NAME,
                GROUP_NAME_VARIABLE_NAME
        });

        PropertyDescriptor fromUserPro = property(FROM_USER);
        fromUserPro.setValue(NOT_UNDEFINED, Boolean.TRUE);
        fromUserPro.setValue(DEFAULT, "");
        fromUserPro.setValue(NOT_EXPRESSION, Boolean.TRUE);

        PropertyDescriptor toUserPro = property(TO_USER);
        toUserPro.setValue(NOT_UNDEFINED, Boolean.TRUE);
        toUserPro.setValue(DEFAULT, "");
        toUserPro.setValue(NOT_EXPRESSION, Boolean.TRUE);

        PropertyDescriptor fromUsernamePro = property(FROM_USERNAME_VARIABLE_NAME);
        fromUsernamePro.setValue(NOT_UNDEFINED, Boolean.TRUE);
        fromUsernamePro.setValue(DEFAULT, "");
        fromUsernamePro.setValue(NOT_EXPRESSION, Boolean.TRUE);

        PropertyDescriptor fromUserPwdPro = property(FROM_USER_PASSWORD_VARIABLE_NAME);
        fromUserPwdPro.setValue(NOT_UNDEFINED, Boolean.TRUE);
        fromUserPwdPro.setValue(DEFAULT, "");
        fromUserPwdPro.setValue(NOT_EXPRESSION, Boolean.TRUE);

        PropertyDescriptor fromDevicePro = property(FROM_DEVICE_VARIABLE_NAME);
        fromDevicePro.setValue(NOT_UNDEFINED, Boolean.TRUE);
        fromDevicePro.setValue(DEFAULT, "");
        fromDevicePro.setValue(NOT_EXPRESSION, Boolean.TRUE);

        PropertyDescriptor fromDeviceTypePro = property(FROM_DEVICE_TYPE_VARIABLE_NAME);
        fromDeviceTypePro.setValue(NOT_UNDEFINED, Boolean.TRUE);
        fromDeviceTypePro.setValue(DEFAULT, "");
        fromDeviceTypePro.setValue(NOT_EXPRESSION, Boolean.TRUE);

        PropertyDescriptor toUsernamePro = property(TO_USERNAME_VARIABLE_NAME);
        toUsernamePro.setValue(NOT_UNDEFINED, Boolean.TRUE);
        toUsernamePro.setValue(DEFAULT, "");
        toUsernamePro.setValue(NOT_EXPRESSION, Boolean.TRUE);

        PropertyDescriptor toDevicePro = property(TO_DEVICE_VARIABLE_NAME);
        toDevicePro.setValue(NOT_UNDEFINED, Boolean.TRUE);
        toDevicePro.setValue(DEFAULT, "");
        toDevicePro.setValue(NOT_EXPRESSION, Boolean.TRUE);

        PropertyDescriptor toDeviceTypePro = property(TO_DEVICE_TYPE_VARIABLE_NAME);
        toDeviceTypePro.setValue(NOT_UNDEFINED, Boolean.TRUE);
        toDeviceTypePro.setValue(DEFAULT, "");
        toDeviceTypePro.setValue(NOT_EXPRESSION, Boolean.TRUE);

        PropertyDescriptor groupPro = property(GROUP);
        groupPro.setValue(NOT_UNDEFINED, Boolean.TRUE);
        groupPro.setValue(DEFAULT, "");
        groupPro.setValue(NOT_EXPRESSION, Boolean.TRUE);

        PropertyDescriptor groupIdPro = property(GROUP_ID_VARIABLE_NAME);
        groupIdPro.setValue(NOT_UNDEFINED, Boolean.TRUE);
        groupIdPro.setValue(DEFAULT, "");
        groupIdPro.setValue(NOT_EXPRESSION, Boolean.TRUE);

        PropertyDescriptor groupNamePro = property(GROUP_NAME_VARIABLE_NAME);
        groupNamePro.setValue(NOT_UNDEFINED, Boolean.TRUE);
        groupNamePro.setValue(DEFAULT, "");
        groupNamePro.setValue(NOT_EXPRESSION, Boolean.TRUE);
    }
}
