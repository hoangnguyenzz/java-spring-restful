package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill createSkill(Skill skill) throws IdInvalidException {
        boolean isExist = this.skillRepository.existsByName(skill.getName());
        if (isExist) {
            throw new IdInvalidException("Skill name = " + skill.getName() + " already exists");
        }
        return skillRepository.save(skill);
    }

    public Skill updateSkill(Skill skill) throws IdInvalidException {
        Skill skill2 = this.getSkillById(skill.getId());
        skill2.setName(skill.getName());
        return skillRepository.save(skill2);
    }

    public Skill getSkillById(Long id) throws IdInvalidException {
        return this.skillRepository.findById(id).orElseThrow(
                () -> new IdInvalidException("Skill id = " + id + " not found"));
    }

    public ResultPaginationDTO getAllSkill(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> skills = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();

        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(skills.getNumber() + 1);
        mt.setPageSize(skills.getSize());
        mt.setPages(skills.getTotalPages());
        mt.setTotal(skills.getTotalElements());
        rs.setMeta(mt);
        rs.setResult(skills.getContent());
        return rs;

    }

    public void deleteSkill(Long id) throws IdInvalidException {
        Skill skill = this.getSkillById(id);

        skill.getJobs().forEach(job -> {
            job.getSkills().remove(skill);
        });
        this.skillRepository.delete(skill);
    }
}
