package com.odeyalo.sonata.authentication.repository;

import com.odeyalo.sonata.authentication.entity.User;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * UserRepository implementation that stores the users in the memory
 */
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users;
    private final AtomicLong idHolder = new AtomicLong();

    public InMemoryUserRepository() {
        this.users = new ConcurrentHashMap<>();
    }

    public InMemoryUserRepository(List<User> users) {
        Assert.notNull(users, "The users should not be null!");
        this.users = users.stream().collect(Collectors.toMap(User::getId, Function.identity()));
        this.idHolder.set(users.size());
    }

    public InMemoryUserRepository(User[] users) {
        this(List.of(users));
    }

    @Override
    public User findUserByEmail(String email) {
        for (Map.Entry<Long, User> entry : users.entrySet()) {
            User user = entry.getValue();
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public <T extends User> T save(User user) {
        Long id = resolveId(user);
        user.setId(id);
        this.users.put(id, user);
        return (T) user;
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    @Override
    public long count() {
        return users.size();
    }

    private Long resolveId(User user) {
        return Optional.ofNullable(user.getId())
                .map(id -> {
                    idHolder.set(id);
                    return id;
                }).orElseGet(idHolder::incrementAndGet);
    }
}
