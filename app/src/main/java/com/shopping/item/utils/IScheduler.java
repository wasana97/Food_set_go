package com.shopping.item.utils;

import io.reactivex.Scheduler;

public interface IScheduler {

    Scheduler mainThread();

    Scheduler backgroundThread();
}
