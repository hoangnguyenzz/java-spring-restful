package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    public Company createCompany(Company request) {

        // Company company = new Company();
        // company.setName(request.getName());
        // company.setAddress(request.getAddress());
        // company.setDescription(request.getDescription());
        // company.setLogo(request.getLogo());
        return companyRepository.save(request);

    }

    public ResultPaginationDTO getAllCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> pCompany = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pCompany.getTotalPages());
        mt.setTotal(pCompany.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pCompany.getContent());
        return rs;
    }

    public Company getCompanyById(Long id) {
        Optional<Company> company = this.companyRepository.findById(id);
        if (company.isPresent()) {
            return company.get();
        }
        return null;
    }

    public Company updateCompanyById(Company request) {
        Company company = getCompanyById(request.getId());
        if (company != null) {
            company.setName(request.getName());
            company.setAddress(request.getAddress());
            company.setDescription(request.getDescription());
            company.setLogo(request.getLogo());

        }

        return this.companyRepository.save(company);
    }

    public void deleteCompanyById(Long id) {
        Optional<Company> company = this.companyRepository.findById(id);
        if (company.isPresent()) {
            List<User> users = company.get().getUsers();
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }
}
