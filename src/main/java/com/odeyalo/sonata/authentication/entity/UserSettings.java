package com.odeyalo.sonata.authentication.entity;

import com.odeyalo.sonata.authentication.entity.settings.UserMfaSettings;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private User user;
    @JoinColumn(name = "mfa_settings_id", updatable = false, nullable = false)
    @OneToOne
    private UserMfaSettings userMfaSettings = UserMfaSettings.empty(user);
}
