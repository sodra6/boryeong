package com.mindone.Boryeongapi.domain.entity.main;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QValve is a Querydsl query type for Valve
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QValve extends EntityPathBase<Valve> {

    private static final long serialVersionUID = -700946546L;

    public static final QValve valve = new QValve("valve");

    public final DateTimePath<java.util.Date> coltDt = createDateTime("coltDt", java.util.Date.class);

    public final StringPath id = createString("id");

    public final StringPath val = createString("val");

    public QValve(String variable) {
        super(Valve.class, forVariable(variable));
    }

    public QValve(Path<? extends Valve> path) {
        super(path.getType(), path.getMetadata());
    }

    public QValve(PathMetadata metadata) {
        super(Valve.class, metadata);
    }

}

