package DriveMate.drivemate.service;

import DriveMate.drivemate.domain.Section;
import DriveMate.drivemate.domain.User;
import DriveMate.drivemate.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionService {

    @Autowired
    private SectionRepository sectionRepository;

    public Section validateDuplicateSection(String sectionName) {
        List<Section> findSections = sectionRepository.findBySectionName(sectionName);
        if (!findSections.isEmpty())
            return findSections.get(0);
        return new Section();
    }

    public Section findBySectionName(String sectionName){
        return sectionRepository.findBySectionName(sectionName).get(0);
    }
}
