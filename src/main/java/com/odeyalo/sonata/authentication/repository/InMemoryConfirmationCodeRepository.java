package com.odeyalo.sonata.authentication.repository;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * {@link ConfirmationCodeRepository} implementation that using Map to store elements
 * and store elements in RAM
 */
public class InMemoryConfirmationCodeRepository implements ConfirmationCodeRepository {
    // Key - id of the ConfirmationCode, value - ConfirmationCode value
    private final Map<Long, ConfirmationCode> idValueStore;
    // Key - confirmation code value, value - confirmation code
    private final Map<String, ConfirmationCode> codeValueStore;

    private final AtomicLong idHolder = new AtomicLong();

    public InMemoryConfirmationCodeRepository() {
        this.idValueStore = new ConcurrentHashMap<>();
        codeValueStore = new ConcurrentHashMap<>();
    }

    /**
     * Create a new InMemoryConfirmationCodeRepository with predefined ConfirmationCode(s).
     * The values MUST contain id of the ConfirmationCode.
     * @param idValueStore - predefined entities to save
     */
    public InMemoryConfirmationCodeRepository(Map<Long, ConfirmationCode> idValueStore) {
        this.idValueStore = new ConcurrentHashMap<>(idValueStore);
        this.codeValueStore = idValueStore.entrySet().stream()
                .collect(Collectors.toMap(
                        (entry) -> entry.getValue().getCode(),
                        Map.Entry::getValue
                ));
        this.idHolder.set(idValueStore.size());
    }

    @Override
    public Optional<ConfirmationCode> findById(Long id) {
        ConfirmationCode confirmationCode = idValueStore.get(id);
        return Optional.ofNullable(confirmationCode);
    }

    @Override
    public Optional<ConfirmationCode> findConfirmationCodeByCodeValue(String codeValue) {
        ConfirmationCode confirmationCode = codeValueStore.get(codeValue);
        return Optional.ofNullable(confirmationCode);
    }

    @Override
    public <T extends ConfirmationCode> T save(ConfirmationCode code) {
        Assert.notNull(code, "The ConfirmationCode must not be null!");
        Long id = resolveId(code);
        code.setId(id);
        idValueStore.put(id, code);
        codeValueStore.put(code.getCode(), code);
        return (T) code;
    }

    @Override
    public void deleteById(Long id) {
        Assert.notNull(id, "ID must not be null!");
        this.idValueStore.remove(id);
        this.codeValueStore.values().removeIf(code -> Objects.equals(code.getId(), id));
    }

    @Override
    public void deleteByCodeValue(String codeValue) {
        Assert.notNull(codeValue, "Code value must not be null!");
        ConfirmationCode removed = codeValueStore.remove(codeValue);
        if (removed != null) {
            Long id = removed.getId();
            idValueStore.remove(id);
        }
    }

    @Override
    public long count() {
        return codeValueStore.size();
    }

    /**
     * Return the copy of the store
     * @return - copy of the saved entities
     */
    public Map<Long, ConfirmationCode> getAll() {
        return new HashMap<>(idValueStore);
    }

    private Long resolveId(ConfirmationCode user) {
        return Optional.ofNullable(user.getId())
                .map(id -> {
                    idHolder.set(id);
                    return id;
                }).orElseGet(idHolder::incrementAndGet);
    }
}
