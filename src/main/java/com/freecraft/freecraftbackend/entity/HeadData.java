package com.freecraft.freecraftbackend.entity;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity // This tells Hibernate to make a table out of this class
@Data
@Table(name = "chm_userdata")
public class HeadData {

    @Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "heads")
    private int heads;

    @Column(name = "purchased")
    private int purchased;
}
