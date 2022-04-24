package com.freecraft.freecraftbackend.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Entity // This tells Hibernate to make a table out of this class
@Data
@Table(name = "luckperms_players")
public class PermissionPlayerData {
    @Id
    @Column(name = "uuid")
    private String uiid;

    @Column(name = "username")
    private String username;

    @Column(name = "primary_group")
    private String group;


}