package vn.hoidanit.jobhunter.service;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
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
}
