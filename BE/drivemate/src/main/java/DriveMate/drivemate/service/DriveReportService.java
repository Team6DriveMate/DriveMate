package DriveMate.drivemate.service;

import DriveMate.drivemate.domain.DriveReport;
import DriveMate.drivemate.repository.DriveReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DriveReportService {
    @Autowired
    private DriveReportRepository driveReportRepository;

    @Transactional
    public void saveDriveReport(DriveReport driveReport){driveReportRepository.save(driveReport);}
}
