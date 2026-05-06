package mg.hei.federation_agricole.service;

import mg.hei.federation_agricole.model.dto.ActivityMemberAttendance;
import mg.hei.federation_agricole.model.dto.CollectivityActivity;
import mg.hei.federation_agricole.model.dto.CreateActivityMemberAttendance;
import mg.hei.federation_agricole.model.dto.CreateCollectivityActivity;
import mg.hei.federation_agricole.repository.ActivityRepository;
import mg.hei.federation_agricole.repository.CollectivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepo;
    private final CollectivityRepository collectivityRepo;

    public ActivityService(ActivityRepository activityRepo,
                           CollectivityRepository collectivityRepo) {
        this.activityRepo = activityRepo;
        this.collectivityRepo = collectivityRepo;
    }

    public List<CollectivityActivity> create(String collectivityId,
                                             List<CreateCollectivityActivity> list) throws Exception {
        if (collectivityRepo.findById(collectivityId) == null)
            throw new RuntimeException("Collectivity not found: " + collectivityId);

        List<CollectivityActivity> result = new java.util.ArrayList<>();

        for (CreateCollectivityActivity input : list) {
            // Validation : recurrenceRule ET executiveDate ne peuvent pas être tous les deux fournis
            if (input.getExecutiveDate() != null && input.getRecurrenceRule() != null)
                throw new RuntimeException(
                        "Cannot provide both executiveDate and recurrenceRule");

            if (input.getExecutiveDate() == null && input.getRecurrenceRule() == null)
                throw new RuntimeException(
                        "Either executiveDate or recurrenceRule must be provided");

            result.add(activityRepo.save(collectivityId, input));
        }

        return result;
    }

    public List<CollectivityActivity> getActivities(String collectivityId) throws Exception {
        if (collectivityRepo.findById(collectivityId) == null)
            throw new RuntimeException("Collectivity not found: " + collectivityId);

        return activityRepo.findByCollectivity(collectivityId);
    }

    public List<ActivityMemberAttendance> saveAttendance(
            String collectivityId, String activityId,
            List<CreateActivityMemberAttendance> list) throws Exception {

        if (collectivityRepo.findById(collectivityId) == null)
            throw new RuntimeException("Collectivity not found: " + collectivityId);

        return activityRepo.saveAttendance(activityId, list);
    }

    public List<ActivityMemberAttendance> getAttendance(
            String collectivityId, String activityId) throws Exception {

        if (collectivityRepo.findById(collectivityId) == null)
            throw new RuntimeException("Collectivity not found: " + collectivityId);

        return activityRepo.findAttendance(activityId);
    }
}
