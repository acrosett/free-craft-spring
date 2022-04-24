package com.freecraft.freecraftbackend.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity // This tells Hibernate to make a table out of this class
@Table(name = "transaction")
@Data
public class Transaction {

    void Transaction(int id){
        this.id = id;
    }

    @Id
    private Integer id;

    Date creationDate;

    Date executionDate;

    String product;

    Boolean executed = false;

    String uuid;

}
