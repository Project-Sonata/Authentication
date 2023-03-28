package com.odeyalo.sonata.authentication.entity;

import com.odeyalo.sonata.authentication.entity.settings.UserMfaSettings;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_settings")
public class UserSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    @ToString.Exclude
    private User user;
    @JoinColumn(name = "mfa_settings_id", updatable = false, nullable = false)
    @OneToOne(cascade = CascadeType.ALL)
    private UserMfaSettings userMfaSettings;

    public static UserSettings empty(User user) {
        UserMfaSettings emptyMfa = UserMfaSettings.empty(user);
        return UserSettings.builder()
                .user(user)
                .userMfaSettings(emptyMfa)
                .build();
    }
}
