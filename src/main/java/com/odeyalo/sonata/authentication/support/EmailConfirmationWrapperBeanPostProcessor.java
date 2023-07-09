package com.odeyalo.sonata.authentication.support;

import com.odeyalo.sonata.authentication.repository.AdvancedUserRegistrationInfoStore;
import com.odeyalo.sonata.authentication.service.confirmation.EmailConfirmationManager;
import com.odeyalo.sonata.authentication.service.confirmation.EventPublisherEmailConfirmationManager;
import com.odeyalo.sonata.authentication.support.event.publisher.EventPublisher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

@Component
@Order(LOWEST_PRECEDENCE)
public class EmailConfirmationWrapperBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof EmailConfirmationManager) {
            System.out.println("Wrapped the: " + bean);
            EventPublisher publisher = beanFactory.getBean(EventPublisher.class);
            AdvancedUserRegistrationInfoStore store = beanFactory.getBean(AdvancedUserRegistrationInfoStore.class);
            return new EventPublisherEmailConfirmationManager((EmailConfirmationManager) bean, publisher, store);
        }
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
