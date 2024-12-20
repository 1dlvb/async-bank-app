package com.dlvb.asyncbankapp.auditor;

import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * Реализация интерфейса AuditorAware для аудита изменений в базе данных.
 * @author Matushkin Anton
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    /**
     * Возвращает текущего пользователя для аудируемой сущности.
     * <p>
     * @return Optional, содержащий имя текущего пользователя или "defaultUsername", если пользователь не найден.
     */
    @Override
    public @NonNull Optional<String> getCurrentAuditor() {
        return Optional.of("defaultUsername");
    }

}
