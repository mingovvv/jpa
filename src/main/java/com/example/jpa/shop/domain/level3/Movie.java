package com.example.jpa.shop.domain.level3;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
//@DiscriminatorValue("change-value")
public class Movie extends Item {

    private String director;

    private String actor;

}
