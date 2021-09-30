package com.citic.asp.test.loader;

import java.util.List;
import java.util.Random;

/**
 * 生成下一个账号信息
 *
 * @author qcb
 * @date 2021/04/19 20:00.
 */
public class AccountLoadGenerator implements SyntheticLoadGenerator<Account>{

    private List<Account> accountList;

    public AccountLoadGenerator(List<Account> accountList){
        this.accountList = accountList;
    }

    @Override
    public Account next() {
        if(accountList.isEmpty()){
            return new Account();
        }
        Random random = new Random();
        int index = random.nextInt(accountList.size());
        return accountList.get(index);
    }
}
