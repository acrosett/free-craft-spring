package com.freecraft.freecraftbackend.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity // This tells Hibernate to make a table out of this class
@Data
@Table(name = "pm_userdata")
public class PortalData {

    @Id
    @Column(name = "UUID")
    private String uuid;

    @Column(name = "PORTALS")
    private int portals;

}