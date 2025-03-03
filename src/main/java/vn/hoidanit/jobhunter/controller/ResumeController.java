package vn.hoidanit.jobhunter.controller;

import org.springframework.boot.autoconfigure.rsocket.RSocketProperties.Server.Spec;
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
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping
    public ResponseEntity<ResCreateResumeDTO> createResume(@Valid @RequestBody Resume request)
            throws IdInvalidException {
        ResCreateResumeDTO response = this.resumeService.createResume(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume request) throws IdInvalidException {
        ResUpdateResumeDTO response = this.resumeService.updateResume(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResFetchResumeDTO> getResumeById(@PathVariable Long id) throws IdInvalidException {

        return ResponseEntity.ok(this.resumeService.getResumeById(id));
    }

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getAllResume(
            @Filter Specification<Resume> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.resumeService.getAllResume(spec, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResumeById(@PathVariable Long id) {
        this.resumeService.deleteResume(id);
        return ResponseEntity.ok(null);
    }
}
