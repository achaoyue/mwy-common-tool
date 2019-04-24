package com.mwy.event;


import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.Executors;

@Component
public class EventBusCenter implements BeanFactoryPostProcessor {
    private static ConfigurableListableBeanFactory factory;
    private EventBus eventBus = new EventBus();

    private AsyncEventBus asyncEventBus = new AsyncEventBus(Executors.newCachedThreadPool());

    public void postSync(Object event) {
        eventBus.post(event);
    }

    public void postAsync(Object event) {
        postAsync(event,false);
    }

    public void postAsync(Object event,boolean waitTransaction) {
        if(waitTransaction && TransactionSynchronizationManager.isActualTransactionActive()){
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter(){
                @Override
                public void afterCommit() {
                    super.afterCommit();
                    asyncEventBus.post(event);
                }
            });
        }else{
            asyncEventBus.post(event);
        }
    }

    @PostConstruct
    public void init() {
        Map<String, Object> data = factory.getBeansWithAnnotation(EventListener.class);
        for (Map.Entry entry : data.entrySet()) {
            eventBus.register(entry.getValue());
            asyncEventBus.register(entry.getValue());
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
            factory = configurableListableBeanFactory;
    }
}
