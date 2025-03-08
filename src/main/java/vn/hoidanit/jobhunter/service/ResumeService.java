package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.ResumeRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class ResumeService {

    @Autowired
    FilterBuilder fb;

    @Autowired
    private FilterParser filterParser;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository,
            JobRepository jobRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
    }

    public ResultPaginationDTO fetchResumeByUser(Pageable pageable) {
        // query builder
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        FilterNode node = filterParser.parse("email='" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());

        rs.setMeta(mt);

        // remove sensitive data
        List<ResFetchResumeDTO> listResume = pageResume.getContent()
                .stream().map(item -> this.convertToResFetchResumeDTO(item))
                .collect(Collectors.toList());

        rs.setResult(listResume);

        return rs;
    }

    public ResCreateResumeDTO createResume(Resume request) throws IdInvalidException {
        Resume resume = new Resume();
        resume.setEmail(request.getEmail());
        resume.setUrl(request.getUrl());
        resume.setStatus(request.getStatus());
        User user = this.userRepository.findById(request.getUser().getId()).orElseThrow(
                () -> new IdInvalidException("User id :" + request.getUser().getId() + " khong ton tai !"));
        resume.setUser(user);
        Job job = jobRepository.findById(request.getJob().getId()).orElseThrow(
                () -> new IdInvalidException("Job id: " + request.getJob().getId() + " khong ton tai !"));
        resume.setJob(job);
        resume = this.resumeRepository.save(resume);
        ResCreateResumeDTO response = new ResCreateResumeDTO();
        response.setId(resume.getId());
        response.setCreatedAt(resume.getCreatedAt());
        response.setCreatedBy(resume.getCreatedBy());
        return response;

    }

    public ResUpdateResumeDTO updateResume(Resume request) throws IdInvalidException {
        Resume resume = this.resumeRepository.findById(request.getId()).orElseThrow(
                () -> new IdInvalidException("Resume id: " + request.getId() + " khong ton tai !"));
        resume.setStatus(request.getStatus());
        this.resumeRepository.save(resume);
        ResUpdateResumeDTO response = new ResUpdateResumeDTO();
        response.setUpdatedAt(resume.getUpdatedAt());
        response.setUpdatedBy(resume.getUpdatedBy());
        return response;
    }

    public void deleteResume(Long id) {

        this.resumeRepository.deleteById(id);
    }

    public ResFetchResumeDTO getResumeById(Long id) throws IdInvalidException {

        Resume resume = this.resumeRepository.findById(id).orElseThrow(
                () -> new IdInvalidException("Resume id: " + id + " khong ton tai !"));
        return convertToResFetchResumeDTO(resume);
    }

    public ResultPaginationDTO getAllResume(Specification<Resume> spec,
            Pageable pageable) {
        Page<Resume> resumes = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(resumes.getNumber() + 1);
        mt.setPageSize(resumes.getSize());
        mt.setPages(resumes.getTotalPages());
        mt.setTotal(resumes.getTotalElements());
        rs.setMeta(mt);

        List<Resume> resumeList = resumes.getContent();
        List<ResFetchResumeDTO> dtos = new ArrayList<>();
        for (Resume resume : resumeList) {
            ResFetchResumeDTO dto = convertToResFetchResumeDTO(resume);

            dtos.add(dto);

        }
        rs.setResult(dtos);
        return rs;
    }

    public ResFetchResumeDTO convertToResFetchResumeDTO(Resume resume) {
        ResFetchResumeDTO response = new ResFetchResumeDTO();
        response.setId(resume.getId());
        response.setEmail(resume.getEmail());
        response.setUrl(resume.getUrl());
        response.setStatus(resume.getStatus());
        response.setCreatedAt(resume.getCreatedAt());
        response.setUpdatedAt(resume.getUpdatedAt());
        response.setCreatedBy(resume.getCreatedBy());
        response.setUpdatedBy(resume.getUpdatedBy());
        if (resume.getJob() != null) {
            response.setCompanyName(resume.getJob().getCompany().getName());
        }
        response.setUser(new ResFetchResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName()));
        response.setJob(new ResFetchResumeDTO.JobResume(resume.getJob().getId(), resume.getJob().getName()));
        return response;
    }
}
