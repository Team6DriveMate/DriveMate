package DriveMate.drivemate.service;

import DriveMate.drivemate.domain.DriveReport;
import DriveMate.drivemate.domain.SemiRouteLineString;
import DriveMate.drivemate.domain.SemiRoutePoint;
import DriveMate.drivemate.domain.SemiRouteSurvey;
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

    public SemiRouteSurvey createSemiRouteSurvey(DriveReport driveReport, SemiRoutePoint semiRoutePoint, SemiRouteLineString semiRouteLineString){
        SemiRouteSurvey semiRouteSurvey = new SemiRouteSurvey();
        semiRouteSurvey.setDriveReport(driveReport);
        semiRouteSurvey.setSemiRoutePoint(semiRoutePoint);
        semiRouteSurvey.setSemiRouteLineString(semiRouteLineString);

        return semiRouteSurvey;
    }

    public DriveReport getDriveReportById(Long driveReportId){return driveReportRepository.getReferenceById(driveReportId);}

}
