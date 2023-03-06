package com.mindone.Boryeongapi.domain.entity.hmi;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QHmi is a Querydsl query type for Hmi
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QHmi extends EntityPathBase<Hmi> {

    private static final long serialVersionUID = -742623929L;

    public static final QHmi hmi = new QHmi("hmi");

    public final StringPath etc = createString("etc");

    public final DateTimePath<java.util.Date> logTime = createDateTime("logTime", java.util.Date.class);

    public final StringPath tagId = createString("tagId");

    public final StringPath val = createString("val");

    public QHmi(String variable) {
        super(Hmi.class, forVariable(variable));
    }

    public QHmi(Path<? extends Hmi> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHmi(PathMetadata metadata) {
        super(Hmi.class, metadata);
    }

}

