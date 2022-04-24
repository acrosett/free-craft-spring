package com.freecraft.freecraftbackend.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Entity // This tells Hibernate to make a table out of this class
@Data
@Table(name = "luckperms_user_permissions")
public class PermissionData {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "permission")
    private String permission;

    @Column(name = "server")
    private String server = "global";

    @Column(name = "world")
    private String world = "global";

    @Column(name = "contexts")
    private String contexts ="{}";

    @Column(name = "value")
    private int value = 1;

    @Column(name = "expiry")
    private int expiry = 0;


}