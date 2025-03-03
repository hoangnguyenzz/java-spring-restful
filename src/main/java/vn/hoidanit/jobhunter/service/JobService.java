package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class JobService {

    private final JobRepository jobRepository;

    private final SkillRepository skillRepository;

    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
    }

    public ResCreateJobDTO createJob(Job request) {

        Job job = new Job();
        job.setName(request.getName());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setQuantity(request.getQuantity());
        job.setLevel(request.getLevel());
        job.setDescription(request.getDescription());
        job.setStartDate(request.getStartDate());
        job.setEndDate(request.getEndDate());
        job.setActive(request.isActive());
        List<Long> idList = new ArrayList<>();
        for (Skill item : request.getSkills()) {
            idList.add(item.getId());
        }
        List<Skill> skills = skillRepository.findByIdIn(idList);
        job.setSkills(skills);
        // check company
        if (request.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(request.getCompany().getId());
            if (cOptional.isPresent()) {
                job.setCompany(cOptional.get());
            }
        }
        jobRepository.save(job);

        ResCreateJobDTO response = new ResCreateJobDTO();
        response.setName(job.getName());
        response.setLocation(job.getLocation());
        response.setSalary(job.getSalary());
        response.setQuantity(job.getQuantity());
        response.setLevel(job.getLevel());
        response.setDescription(job.getDescription());
        response.setStartDate(job.getStartDate());
        response.setEndDate(job.getEndDate());
        response.setActive(job.isActive());
        response.setCreatedAt(job.getCreatedAt());
        response.setCreatedBy(job.getCreatedBy());

        // Convert List<Skill> to List<String>
        List<String> skillNames = skills.stream()
                .map(Skill::getName)
                .collect(Collectors.toList());
        response.setSkills(skillNames);
        return response;
    }

    public ResUpdateJobDTO updateJob(Job request) throws IdInvalidException {
        Job job = jobRepository.findById(request.getId()).orElseThrow(
                () -> new IdInvalidException("Job not found"));

        job.setName(request.getName());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setQuantity(request.getQuantity());
        job.setLevel(request.getLevel());
        job.setDescription(request.getDescription());
        job.setStartDate(request.getStartDate());
        job.setEndDate(request.getEndDate());
        job.setActive(request.isActive());
        List<Long> idList = new ArrayList<>();
        for (Skill item : request.getSkills()) {
            idList.add(item.getId());
        }
        List<Skill> skills = skillRepository.findByIdIn(idList);
        job.setSkills(skills);
        // check company
        if (request.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(request.getCompany().getId());
            if (cOptional.isPresent()) {
                job.setCompany(cOptional.get());
            }
        }
        jobRepository.save(job);

        ResUpdateJobDTO response = new ResUpdateJobDTO();
        response.setName(job.getName());
        response.setLocation(job.getLocation());
        response.setSalary(job.getSalary());
        response.setQuantity(job.getQuantity());
        response.setLevel(job.getLevel());
        response.setDescription(job.getDescription());
        response.setStartDate(job.getStartDate());
        response.setEndDate(job.getEndDate());
        response.setActive(job.isActive());

        response.setUpdatedAt(job.getUpdatedAt());
        response.setUpdatedBy(job.getUpdatedBy());
        // Convert List<Skill> to List<String>
        List<String> skillNames = skills.stream()
                .map(Skill::getName)
                .collect(Collectors.toList());
        response.setSkills(skillNames);
        return response;
    }

    public Job getJobById(Long id) throws IdInvalidException {
        Job job = jobRepository.findById(id).orElseThrow(
                () -> new IdInvalidException("Job not found"));
        return job;
    }

    public ResultPaginationDTO getAllJob(Specification<Job> spec, Pageable pageable) {
        Page<Job> jobs = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();

        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(jobs.getNumber() + 1);
        mt.setPageSize(jobs.getSize());
        mt.setPages(jobs.getTotalPages());
        mt.setTotal(jobs.getTotalElements());
        rs.setMeta(mt);
        rs.setResult(jobs.getContent());
        return rs;

    }

    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

}
