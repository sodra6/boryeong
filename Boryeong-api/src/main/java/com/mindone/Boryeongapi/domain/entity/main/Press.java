package com.mindone.Boryeongapi.domain.entity.main;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mindone.Boryeongapi.domain.entity.main.key.PressKey;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@IdClass(PressKey.class)
@Table(name="PRESS")
public class Press {
    @Id
    @ApiModelProperty(value = "ID")
    @Column(name="ID")
    private String id;

    @ApiModelProperty(value="값")
    @Column(name="VAL")
    private double val;

    @Id
    @ApiModelProperty(value="수집시간")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    @Column(name="COLT_DT")
    private Date coltDt;
}
