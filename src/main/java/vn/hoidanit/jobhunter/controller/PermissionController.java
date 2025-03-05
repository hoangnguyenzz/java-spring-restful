package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.PermissionService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission)
            throws IdInvalidException {
        // validate permission
        if (permissionService.checkByExistPermission(permission)) {
            throw new IdInvalidException("Permission đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(permission));
    }

    @PutMapping
    public ResponseEntity<Permission> updatePermission(@RequestBody Permission permission) throws IdInvalidException {
        // validate permission
        // if (permissionService.checkByExistPermission(permission)) {
        // throw new IdInvalidException("Permission đã tồn tại");
        // }

        return ResponseEntity.ok(permissionService.update(permission));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permission> fetchPermissionById(@PathVariable Long id) throws IdInvalidException {
        return ResponseEntity.ok(permissionService.fetchPermissionById(id));
    }

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> fetchAllPermission(
            @Filter Specification<Permission> spec, Pageable pageable) {
        return ResponseEntity.ok(permissionService.fetchAllPermission(spec, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) throws IdInvalidException {
        permissionService.deletePermission(id);
        return ResponseEntity.ok(null);
    }
}
