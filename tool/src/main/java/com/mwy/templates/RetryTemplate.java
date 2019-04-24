package com.mwy.templates;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class RetryTemplate<T, R>{
    /**
     * 重试次数
     */
    @Setter
    private int retryCount = 3;
    /**
     * 重试间隔
     */
    @Setter
    private long retryTime = 300;
    /**
     * 错误标记
     */
    private String tag = "";

    public RetryTemplate(String tag) {
        this.tag = tag;
    }

    public RetryTemplate(int retryCount, int retryTime, String tag) {
        this.retryCount = retryCount;
        this.retryTime = retryTime;
        this.tag = tag;
    }

    /**
     * 执行模版
     * @param param
     * @return
     */
    public  R execute(T param) {
        Exception exception = null;
        while (retryCount-- > 0) {
            try {
                return run(param);
            } catch (Exception e) {
                R o = onError(e,tag, param);
                if (o != null) {
                    return o;
                }
                exception = e;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(retryTime);
            } catch (InterruptedException e) {
                R o = onError(e,tag, param);
                if (o != null) {
                    return o;
                }
            }
        }
        throw new RuntimeException(exception);
    }

    /**
     * 执行方法
     * @param param
     * @return
     */
    public abstract  R run(T param);

    /**
     * @param e
     * @param param
     * @return null:继续重试
     * Nonull:使用返回值
     * throw：继续抛出异常
     */
    public R onError(Exception e ,String tag, T param) {
        log.error("some error occur,tag:{}", tag, e);
        return null;
    }
}
