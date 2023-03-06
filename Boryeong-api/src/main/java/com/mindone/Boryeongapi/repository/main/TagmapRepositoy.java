package com.mindone.Boryeongapi.repository.main;

import com.mindone.Boryeongapi.domain.entity.main.Tagmap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagmapRepositoy extends JpaRepository<Tagmap, Long> {
    List<Tagmap> findByKind(String kind);
    List<Tagmap> findByKindAndLine(String kind, String line);
    List<Tagmap> findByKindOrderBySort(String kind);
    List<Tagmap> findByKindAndLineOrderBySort(String kind, String direction);
    List<Tagmap> findByKindAndSort(String kind, int sort);
}
