package com.sippr.monitor.config;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent;
import de.codecentric.boot.admin.server.notify.AbstractStatusChangeNotifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 自定义通知类
 * @author ChenXiangpeng
 */
@Service
@Slf4j
public class CustomizeNotifier extends AbstractStatusChangeNotifier {
    public CustomizeNotifier(InstanceRepository repository) {
        super(repository);
    }

    /**
     * 有事件发生时触发该方法，返回一套事件处理的逻辑代码，该代码中可以自定义以什么方式进行通知
     * @param event
     * @param instance
     * @return lambda
     */
    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        return Mono.fromRunnable(() -> {
            if (event instanceof InstanceStatusChangedEvent) {
                //该事件为服务状态变化的事件
                log.info(instance.getRegistration().getName()+"---"+event.getInstance()+"---"+((InstanceStatusChangedEvent) event).getStatusInfo().getStatus());
            } else {
                //该事件为其他事件
                log.info(instance.getRegistration().getName()+"---"+event.getInstance()+"---"+event.getType());
            }
        });
    }
}
