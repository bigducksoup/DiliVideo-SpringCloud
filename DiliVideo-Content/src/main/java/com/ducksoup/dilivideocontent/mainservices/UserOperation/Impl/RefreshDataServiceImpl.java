package com.ducksoup.dilivideocontent.mainservices.UserOperation.Impl;

import com.ducksoup.dilivideocontent.mainservices.UserOperation.RefreshDataService;
import com.ducksoup.dilivideocontent.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


@Service
public class RefreshDataServiceImpl implements RefreshDataService {


    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void refreshData(long data, String redisLockKey, Consumer<Long> RefreshOperation) {

        if (redisUtil.exists(redisLockKey)){
            return;
        }

        redisUtil.set(redisLockKey, data, 5L, TimeUnit.MINUTES);
        RefreshOperation.accept(data);
    }

    @Override
    public void refreshData(long data, String redisLockKey, Consumer<Long> RefreshOperation, long expireTime, TimeUnit timeUnit) {
        if (redisUtil.exists(redisLockKey)){
            return;
        }

        redisUtil.set(redisLockKey, data, expireTime, timeUnit);
        RefreshOperation.accept(data);
    }


}
