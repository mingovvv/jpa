package com.example.jpa.shop.domain.level3;

import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Data
//@DiscriminatorValue("change-value")
public class Book extends Item {

    private String author;
    private String isbn;

}
