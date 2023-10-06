package com.example.jpa.shop.domain.level1;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
//@Data
@EqualsAndHashCode
@Getter
@Setter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    // 기본생성자 필수
    public Address() {
    }
}
