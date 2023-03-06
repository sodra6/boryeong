package com.mindone.Boryeongapi.domain.entity.main;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QDepth is a Querydsl query type for Depth
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QDepth extends EntityPathBase<Depth> {

    private static final long serialVersionUID = -717446975L;

    public static final QDepth depth = new QDepth("depth");

    public final DateTimePath<java.util.Date> coltDt = createDateTime("coltDt", java.util.Date.class);

    public final StringPath id = createString("id");

    public final NumberPath<Double> val = createNumber("val", Double.class);

    public QDepth(String variable) {
        super(Depth.class, forVariable(variable));
    }

    public QDepth(Path<? extends Depth> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDepth(PathMetadata metadata) {
        super(Depth.class, metadata);
    }

}

