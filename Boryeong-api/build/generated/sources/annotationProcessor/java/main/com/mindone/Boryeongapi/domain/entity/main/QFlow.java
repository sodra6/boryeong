package com.mindone.Boryeongapi.domain.entity.main;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QFlow is a Querydsl query type for Flow
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QFlow extends EntityPathBase<Flow> {

    private static final long serialVersionUID = 1223848816L;

    public static final QFlow flow = new QFlow("flow");

    public final DateTimePath<java.util.Date> coltDt = createDateTime("coltDt", java.util.Date.class);

    public final StringPath id = createString("id");

    public final NumberPath<Double> totalVal = createNumber("totalVal", Double.class);

    public final NumberPath<Double> val = createNumber("val", Double.class);

    public QFlow(String variable) {
        super(Flow.class, forVariable(variable));
    }

    public QFlow(Path<? extends Flow> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFlow(PathMetadata metadata) {
        super(Flow.class, metadata);
    }

}

