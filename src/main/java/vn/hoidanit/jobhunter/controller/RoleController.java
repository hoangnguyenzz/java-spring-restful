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

import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) throws IdInvalidException {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(role));
    }

    @PutMapping
    public ResponseEntity<Role> updateRole(@RequestBody Role role) throws IdInvalidException {
        // validate permission
        // if (permissionService.checkByExistPermission(permission)) {
        // throw new IdInvalidException("Permission đã tồn tại");
        // }

        return ResponseEntity.ok(roleService.update(role));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> fetchRoleById(@PathVariable Long id) throws IdInvalidException {
        return ResponseEntity.ok(roleService.fetchById(id));
    }

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> fetchAllPermission(
            @Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.ok(roleService.fetchAll(spec, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) throws IdInvalidException {
        roleService.deleteRole(id);
        return ResponseEntity.ok(null);
    }
}
