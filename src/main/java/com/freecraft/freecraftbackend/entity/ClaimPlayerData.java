package com.freecraft.freecraftbackend.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Entity // This tells Hibernate to make a table out of this class
@Data
@Table(name = "griefprevention_playerdata")
public class ClaimPlayerData {

    @Id
    @Column(name = "name")
    private String uuid;

    @Column(name = "lastlogin")
    private Date lastLogin;

    @Column(name = "accruedblocks")
    private int accruedBlocks;

    @Column(name = "bonusblocks")
    private int bonusBlocks;

}