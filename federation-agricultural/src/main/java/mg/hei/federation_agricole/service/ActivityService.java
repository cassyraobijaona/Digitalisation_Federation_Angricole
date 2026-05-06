package mg.hei.federation_agricole.service;

import mg.hei.federation_agricole.model.dto.*;
import mg.hei.federation_agricole.repository.ActivityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepo;

    public ActivityService(ActivityRepository activityRepo) {
        this.activityRepo = activityRepo;
    }

    public List<CollectivityActivity> createActivities(
            String collectivityId,
            List<CreateCollectivityActivity> activities) {
        try {
            return activityRepo.saveAll(collectivityId, activities);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public List<CollectivityActivity> getActivities(String collectivityId) {
        try {
            return activityRepo.findByCollectivity(collectivityId);
        } catch (SQLException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public List<ActivityMemberAttendance> createAttendance(
            String activityId,
            List<CreateActivityMemberAttendance> attendances) {
        try {
            return activityRepo.saveAttendance(activityId, attendances);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public List<ActivityMemberAttendance> getAttendance(String activityId) {
        try {
            return activityRepo.findAttendance(activityId);
        } catch (SQLException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}