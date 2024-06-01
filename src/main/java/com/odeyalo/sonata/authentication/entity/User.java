package com.odeyalo.sonata.authentication.entity;

import com.odeyalo.sonata.authentication.entity.settings.UserSettings;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import java.util.UUID;

/**
 * Entity class to store the data about end-user
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String sonataId;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "password", nullable = false, length = 3000)
    private String password;
    @Column(name = "is_activated", nullable = false)
    private boolean active;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserSettings userSettings;
    @NaturalId
    private String naturalId;

    public User(Long id, String email, String password, boolean active) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.active = active;
    }


    @PrePersist
    public void generateNaturalId() {
        if (naturalId == null) {
            naturalId = UUID.randomUUID().toString();
        }
        if (userSettings == null) {
            userSettings = UserSettings.empty(this);
        }
    }
}
