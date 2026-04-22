package mg.hei.federation_agricole.controller;

import mg.hei.federation_agricole.model.dto.Collectivity;
import mg.hei.federation_agricole.model.dto.CreateCollectivity;
import mg.hei.federation_agricole.service.CollectivityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class CollectivityController {

    private final CollectivityService collectivityService;

    public CollectivityController(CollectivityService collectivityService) {
        this.collectivityService = collectivityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<Collectivity> createCollectivities(
            @RequestBody List<CreateCollectivity> requests) {
        return collectivityService.createCollectivities(requests);
    }
}