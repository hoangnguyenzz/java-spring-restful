package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    public List<Permission> convertToListPermission(Role role) {

        List<Long> permissionsId = role.getPermissions()
                .stream().map(x -> x.getId()).toList();
        List<Permission> permissions = this.permissionRepository.findByIdIn(permissionsId);

        return permissions;
    }

    public Role create(Role role) throws IdInvalidException {
        boolean roleExist = roleRepository.existsByName(role.getName());
        if (roleExist) {
            throw new IdInvalidException("Role name already exists");
        }
        if (role.getPermissions() != null) {

            role.setPermissions(convertToListPermission(role));
        }

        return roleRepository.save(role);
    }

    public Role update(Role role) throws IdInvalidException {

        Role roleDb = roleRepository.findById(role.getId()).orElseThrow(
                () -> new IdInvalidException("Role not found with id: " + role.getId()));
        if (role.getPermissions() != null) {

            role.setPermissions(convertToListPermission(role));
        }
        roleDb.setName(role.getName());
        roleDb.setPermissions(role.getPermissions());
        roleDb.setDescription(role.getDescription());
        roleDb.setActive(role.isActive());

        return roleRepository.save(roleDb);
    }

    public Role fetchById(Long id) throws IdInvalidException {
        return roleRepository.findById(id).orElseThrow(
                () -> new IdInvalidException("Role not found with id: " + id));

    }

    public ResultPaginationDTO fetchAll(Specification<Role> spec, Pageable pageable) {
        Page<Role> roles = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();

        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(roles.getNumber() + 1);
        mt.setPageSize(roles.getSize());
        mt.setPages(roles.getTotalPages());
        mt.setTotal(roles.getTotalElements());
        rs.setMeta(mt);
        rs.setResult(roles.getContent());
        return rs;
    }

    public void deleteRole(long id) {
        this.roleRepository.deleteById(id);
    }
}
