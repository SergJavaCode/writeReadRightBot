package ru.taksebe.telegram.writeRead.initialization;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * Загрузчик в БД словарей по умолчанию при инициализации приложения
 */
@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class InitializingBeanImpl implements InitializingBean {

    @Override
    public void afterPropertiesSet() {

    }
}