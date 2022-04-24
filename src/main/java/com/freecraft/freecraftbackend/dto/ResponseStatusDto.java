package com.freecraft.freecraftbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Date;

@Data
public class ResponseStatusDto {

    boolean member;

    boolean unlocked;

    String uuid = null;

    @JsonFormat(pattern="dd/MM/yy")
    Date lastLogin;
}
