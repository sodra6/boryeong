package com.mindone.Boryeongapi.domain.entity.main;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mindone.Boryeongapi.domain.entity.main.key.DepthKey;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@IdClass(DepthKey.class)
@Table(name="DEPTH")
public class Depth {
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
