package com.example.bankcards.util.data.account.role;

import com.example.bankcards.entity.Role;

public class RoleData {
    public static final Role DEFAULT_ROLE = role().build();

    private RoleData() {
    }

    public static RoleBuilder role() {
        return new RoleBuilder();
    }

    public static class RoleBuilder extends BaseRoleBuilder<RoleBuilder> {
        private RoleBuilder() {
        }

        @Override
        protected RoleBuilder self() {
            return this;
        }

        public Role build() {
            Role r = new Role();
            r.setId(id);
            r.setName(name);
            return r;
        }
    }
}
