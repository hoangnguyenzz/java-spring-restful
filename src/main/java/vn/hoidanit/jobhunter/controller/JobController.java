package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping()
    public ResponseEntity<ResCreateJobDTO> createJob(@Valid @RequestBody Job request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.createJob(request));
    }

    @PutMapping()
    public ResponseEntity<ResUpdateJobDTO> updateJob(@Valid @RequestBody Job request)
            throws IdInvalidException {

        return ResponseEntity.ok(this.jobService.updateJob(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) throws IdInvalidException {
        return ResponseEntity.ok(this.jobService.getJobById(id));
    }

    @GetMapping()
    public ResponseEntity<ResultPaginationDTO> getJobs(
            @Filter Specification<Job> spec, Pageable pageable) {
        return ResponseEntity.ok(this.jobService.getAllJob(spec, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        this.jobService.deleteJob(id);
        return ResponseEntity.ok(null);
    }
}
