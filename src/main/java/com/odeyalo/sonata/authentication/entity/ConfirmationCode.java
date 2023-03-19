package com.odeyalo.sonata.authentication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Entity
@Table(name = "confirmation_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String codeValue;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @Column(name = "is_activated", nullable = false)
    private boolean activated = false;

    @Column(name = "lifecycle_stage", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private LifecycleStage lifecycleStage = LifecycleStage.CREATED;

    @JoinColumn(name = "user_id", nullable = false)
    @OneToOne
    private User user;

    public String getCode() {
        return codeValue;
    }

    public boolean isExpired() {
        return expirationTime.isBefore(now());
    }

    public enum LifecycleStage {
        /**
         * First stage of the lifecycle when the token has been only created
         */
        CREATED,
        /**
         * Lifecycle stage when the confirmation code is attempted to activate.
         * This is useful when confirmation code is associated with a user's session and the generated code can only be activated from the same session
         */
        ATTEMPTED,
        /**
         * If ConfirmationCode has been successfully activated. In this case, code will not be longer valid(Can be removed from storage)
         */
        ACTIVATED,
        /**
         * Opposite to ACTIVATED stage,  there was an attempt to activate the confirmation code but code is no longer valid.
         * For example, code that associated with specific session can only be attempted 3 times,
         * if user enter code incorrect 3 times,
         * then this code will be marked as DENIED and new code must be generated
         */
        DENIED
    }
}
