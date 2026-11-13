package com.irfan.entity;

import jakarta.persistence.*;
import lombok.*;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {


    @Id
    private String roleId;

    @Column(unique = true,nullable = false)
    private String roleName;

    @PrePersist
    public void ensureId() {
        if (this.roleId == null) {
            this.roleId = UUID.randomUUID().toString();
        }
    }



}
