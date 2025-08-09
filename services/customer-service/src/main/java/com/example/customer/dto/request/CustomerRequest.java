package com.example.customer.dto.request;

import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Data @AllArgsConstructor @NoArgsConstructor
public final class CustomerRequest {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CustomerRequest) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.username, that.username) &&
                Objects.equals(this.firstName, that.firstName) &&
                Objects.equals(this.lastName, that.lastName) &&
                Objects.equals(this.email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, firstName, lastName, email);
    }

    @Override
    public String toString() {
        return "CustomerRequest[" +
                "id=" + id + ", " +
                "username=" + username + ", " +
                "firstName=" + firstName + ", " +
                "lastName=" + lastName + ", " +
                "email=" + email + ']';
    }

}
