package com.odeyalo.sonata.authentication.dto;

import com.odeyalo.sonata.authentication.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic user info to return after the user has been successfully registered
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo {
    private String id;
    private String email;

    public static UserInfo from(User user) {
        return new UserInfo(String.valueOf(user.getId()), user.getEmail());
    }
}
