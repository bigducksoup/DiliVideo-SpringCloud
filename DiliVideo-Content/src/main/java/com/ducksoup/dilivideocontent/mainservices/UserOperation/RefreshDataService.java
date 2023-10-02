package com.ducksoup.dilivideocontent.mainservices.UserOperation;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface RefreshDataService {



    void refreshData(long data, String redisLockKey, Consumer<Long> RefreshOperation);


    void refreshData(long data, String redisLockKey, Consumer<Long> RefreshOperation, long expireTime, TimeUnit timeUnit);
}
