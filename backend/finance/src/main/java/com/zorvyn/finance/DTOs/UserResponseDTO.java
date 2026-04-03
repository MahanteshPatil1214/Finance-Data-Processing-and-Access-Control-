package com.zorvyn.finance.DTOs;

import com.zorvyn.finance.model.Role;
import com.zorvyn.finance.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String displayId;
    private String email;
    private Role role;
    private boolean active;

    public UserResponseDTO(User user) {
        this.displayId = user.getDisplayId();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.active = user.isActive();
    }
}
