package com.example.jpa.shop.domain.level1;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
@Data
@EqualsAndHashCode
public class Period {

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // 기본생성자 필수
    public Period() {
    }
}
