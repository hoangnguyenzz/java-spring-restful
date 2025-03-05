package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import javax.naming.spi.DirStateFactory.Result;

import org.springframework.boot.autoconfigure.rsocket.RSocketProperties.Server.Spec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean checkByExistPermission(Permission permission) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(permission.getModule(),
                permission.getApiPath(),
                permission.getMethod());
    }

    public Permission create(Permission permission) throws IdInvalidException {
        return this.permissionRepository.save(permission);
    }

    public Permission update(Permission permission) throws IdInvalidException {
        Permission permissionInDb = permissionRepository.findById(permission.getId())
                .orElseThrow(() -> new IdInvalidException("Permission id = " + permission.getId() + " không tồn tại"));

        if (this.checkByExistPermission(permission)) {
            throw new IdInvalidException("Permission đã tồn tại");
        }
        permissionInDb.setModule(permission.getModule());
        permissionInDb.setApiPath(permission.getApiPath());
        permissionInDb.setMethod(permission.getMethod());
        permissionInDb.setName(permission.getName());

        return permissionRepository.save(permission);
    }

    public Permission fetchPermissionById(Long id) throws IdInvalidException {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Permission id = " + id + " không tồn tại"));
    }

    public ResultPaginationDTO fetchAllPermission(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> page = permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(page.getNumber() + 1);
        mt.setPageSize(page.getSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());
        rs.setMeta(mt);
        rs.setResult(page.getContent());

        return rs;
    }

    public void deletePermission(Long id) {
        // delete permission_role
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        Permission currentPermission = permissionOptional.get();
        currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));

        // delete permission
        this.permissionRepository.delete(currentPermission);
    }
}
