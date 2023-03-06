package com.mindone.Boryeongapi.domain.entity.main;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QTagmap is a Querydsl query type for Tagmap
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTagmap extends EntityPathBase<Tagmap> {

    private static final long serialVersionUID = -311922364L;

    public static final QTagmap tagmap = new QTagmap("tagmap");

    public final StringPath id = createString("id");

    public final StringPath kind = createString("kind");

    public final StringPath line = createString("line");

    public final StringPath name = createString("name");

    public final NumberPath<Integer> sort = createNumber("sort", Integer.class);

    public final StringPath tagId = createString("tagId");

    public QTagmap(String variable) {
        super(Tagmap.class, forVariable(variable));
    }

    public QTagmap(Path<? extends Tagmap> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTagmap(PathMetadata metadata) {
        super(Tagmap.class, metadata);
    }

}

