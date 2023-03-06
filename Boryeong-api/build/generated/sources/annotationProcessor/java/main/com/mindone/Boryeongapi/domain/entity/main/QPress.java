package com.mindone.Boryeongapi.domain.entity.main;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QPress is a Querydsl query type for Press
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPress extends EntityPathBase<Press> {

    private static final long serialVersionUID = -705988031L;

    public static final QPress press = new QPress("press");

    public final DateTimePath<java.util.Date> coltDt = createDateTime("coltDt", java.util.Date.class);

    public final StringPath id = createString("id");

    public final NumberPath<Double> val = createNumber("val", Double.class);

    public QPress(String variable) {
        super(Press.class, forVariable(variable));
    }

    public QPress(Path<? extends Press> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPress(PathMetadata metadata) {
        super(Press.class, metadata);
    }

}

