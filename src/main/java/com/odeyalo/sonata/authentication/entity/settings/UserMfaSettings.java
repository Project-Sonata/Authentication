package com.odeyalo.sonata.authentication.entity.settings;

import com.odeyalo.sonata.authentication.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Builder
@Table(name = "user_mfa_settings")
public class UserMfaSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @JoinTable(name = "user_mfa_settings_authorized_mfa_types")
    @ElementCollection(targetClass = MfaType.class)
    @Singular
    private Set<MfaType> authorizedMfaTypes;
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    @OneToOne
    @ToString.Exclude
    private User user;

    public UserMfaSettings(Long id, Set<MfaType> authorizedMfaTypes, User user) {
        this.id = id;
        this.authorizedMfaTypes = new HashSet<>(authorizedMfaTypes);
        this.user = user;
    }

    public static UserMfaSettings empty(User user) {
        return UserMfaSettings.builder()
                .authorizedMfaType(MfaType.NONE)
                .user(user)
                .build();
    }

    public void addAuthorizedMfaType(MfaType mfaType) {
        if (authorizedMfaTypes == null) {
            this.authorizedMfaTypes = new HashSet<>();
        }
        // Remove default value since user updates the preferred type
        this.authorizedMfaTypes.remove(MfaType.NONE);
        this.authorizedMfaTypes.add(mfaType);
    }

    public boolean isEnabled() {
        return authorizedMfaTypes != null && !authorizedMfaTypes.contains(MfaType.NONE);
    }

    public enum MfaType {
        /**
         * If MFA should be not enabled.
         */
        NONE,
        /**
         * MFA using TOTP(time-based one time password) using Google Authenticator or other application
         */
        TOTP,
        /**
         * MFA using email message
         */
        EMAIL;
    }
}
