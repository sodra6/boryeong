package com.mindone.Boryeongapi.domain.entity.main;

import com.mindone.Boryeongapi.domain.entity.main.key.TagmapKey;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@IdClass(TagmapKey.class)
@Table(name="TAGMAP")
public class Tagmap {

    @Id
    @ApiModelProperty(value = "ID")
    @Column(name="ID")
    private String id;

    @Id
    @ApiModelProperty(value = "태그ID")
    @Column(name="TAG_ID")
    private String tagId;

    @ApiModelProperty(value="이름")
    @Column(name="NAME")
    private String name;

    @ApiModelProperty(value="방향")
    @Column(name="LINE")
    private String line;

    @ApiModelProperty(value="종류")
    @Column(name="KIND")
    private String kind;

    @ApiModelProperty(value="정렬")
    @Column(name="SORT")
    private int sort;

    @Builder
    public Tagmap(String tagId, String name, String line, String kind, int sort) {
        this.tagId = tagId;
        this.name = name;
        this.line = line;
        this.kind = kind;
        this.sort = sort;
    }
}
