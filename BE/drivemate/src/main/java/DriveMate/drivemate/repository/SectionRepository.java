package DriveMate.drivemate.repository;

import DriveMate.drivemate.domain.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findBySectionName(String sectionName);
}
