package com.odeyalo.sonata.authentication.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Entity class to store the data about end-user
 */
@Data
@AllArgsConstructor
public class User {
    private Long id;
    private String email;
    private boolean active;
}
