package com.irfan.entity;



import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")

public class User {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private String userId;

    @Column(name = "user_name")
    private String name;

    @Column(name = "user_email", unique = true)
    private String email;

    @Column(name = "user_password", length = 500)
    private String password;

    private String gender;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    @Column(length = 1000)
    private String about;

    @Column(name = "user_image_name")
    private String imageName;

    private String verifyOtp;

    private Boolean isAccountVerifiedAt;

    private LocalDateTime verifyOtpExpiredAt;

    private String resetOtp;

    private LocalDateTime resetOtpExpiredAt;

    private String qrCodeToken;




    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();





    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;     // always set on create
        this.updatedAt = now;     // always set on create
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();  // update only on update
    }







}
