package com.example.bankcards.util.data.account;

import com.example.bankcards.entity.Role;
import com.example.bankcards.util.data.account.role.RoleData;

public abstract class BaseAccountBuilder<T extends BaseAccountBuilder<T>> {
    protected Long id;
    protected String username = "user";
    protected String password = "pass";
    protected String firstName = "First";
    protected String lastName = "Last";
    protected String email = "user@example.com";
    protected String phone = "+70000000000";
    protected Role role = RoleData.DEFAULT_ROLE;
    protected Integer roleId = 1;

    public T withId(Long id) {
        this.id = id;
        return self();
    }

    public T withUsername(String username) {
        this.username = username;
        return self();
    }

    public T withPassword(String password) {
        this.password = password;
        return self();
    }

    public T withFirstName(String firstName) {
        this.firstName = firstName;
        return self();
    }

    public T withLastName(String lastName) {
        this.lastName = lastName;
        return self();
    }

    public T withEmail(String email) {
        this.email = email;
        return self();
    }

    public T withPhone(String phone) {
        this.phone = phone;
        return self();
    }

    public T withRole(Role role) {
        this.role = role;
        return self();
    }

    public T withRoleId(Integer roleId) {
        this.roleId = roleId;
        return self();
    }

    protected abstract T self();
}
