package com.odeyalo.sonata.authentication.service.mfa;

import com.odeyalo.sonata.authentication.entity.User;
import lombok.Data;
import org.springframework.util.Assert;


/**
 * A simple data class that contains information about specific mfa method.
 *
 * Class is not final to add ability to create a sub-method
 */
@Data
public class MfaMethodInfo {
    protected final String type;
    protected final User user;
    protected final String content;

    /**
     * @param type - type of the method, it can be 'email', 'qrcode', 'mobile_app' and so on.
     * @param user - user that need to confirm his identity through MFA by specific mfa type.
     * @param content - string representation of the content. If you need to return image you should to convert it to Base64.
     */
    public MfaMethodInfo(String type, User user, String content) {
        Assert.notNull(type, "Type must be presented!");
        Assert.notNull(user, "User must be presented!");
        Assert.notNull(content, "Content must be presented!");
        this.type = type;
        this.user = user;
        this.content = content;
    }

    public static MfaMethodInfo of(String type, User user, String content) {
        return new MfaMethodInfo(type, user, content);
    }
}
