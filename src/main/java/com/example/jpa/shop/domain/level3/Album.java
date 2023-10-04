package com.example.jpa.shop.domain.level3;

import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Data
//@DiscriminatorValue("change-value")
public class Album extends Item {

    private String artist;

}
