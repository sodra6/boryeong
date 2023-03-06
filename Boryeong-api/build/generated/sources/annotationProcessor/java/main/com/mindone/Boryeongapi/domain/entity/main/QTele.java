package com.mindone.Boryeongapi.domain.entity.main;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QTele is a Querydsl query type for Tele
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTele extends EntityPathBase<Tele> {

    private static final long serialVersionUID = 1224259052L;

    public static final QTele tele = new QTele("tele");

    public final DateTimePath<java.util.Date> coltDt = createDateTime("coltDt", java.util.Date.class);

    public final StringPath id = createString("id");

    public final StringPath val = createString("val");

    public QTele(String variable) {
        super(Tele.class, forVariable(variable));
    }

    public QTele(Path<? extends Tele> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTele(PathMetadata metadata) {
        super(Tele.class, metadata);
    }

}

