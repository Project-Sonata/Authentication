package com.odeyalo.sonata.authentication.dto;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.common.authentication.dto.UserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Generic user info to return after the user has been successfully registered
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ExtendedUserInfo extends UserInfo {

    public ExtendedUserInfo(String id, String email) {
        super(id, email);
    }

    public static ExtendedUserInfo from(User user) {
        return new ExtendedUserInfo(String.valueOf(user.getId()), user.getEmail());
    }
}
