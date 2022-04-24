package com.freecraft.freecraftbackend.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity // This tells Hibernate to make a table out of this class
@Data
@Table(name = "fch_userdata")
public class FchData {

    @Id
    @Column(name = "UUID")
    private String uuid;

    @Column(name = "AGE_CHECKED")
    private boolean ageChecked;

    @Column(name = "EULA_ACCEPT")
    private boolean eulaAccept;

    @Column(name = "ONLINE")
    private boolean online;

    @Column(name = "BUYING")
    private boolean buying;

    @Column(name = "UNLOCKED")
    private boolean unlocked;

}
