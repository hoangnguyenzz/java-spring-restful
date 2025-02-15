package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.dto.UpdateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.UserDTO;
import vn.hoidanit.jobhunter.domain.dto.CreateUserDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CreateUserDTO createUser(User user) throws IdInvalidException {

        if (this.userRepository.existsByEmail(user.getEmail())) {
            throw new IdInvalidException("Email " + user.getEmail() + " đã tồn tại !");
        }

        User user1 = this.userRepository.save(user);

        CreateUserDTO userDTO = new CreateUserDTO();

        userDTO.setId(user1.getId());
        userDTO.setName(user1.getName());
        userDTO.setGender(user1.getGender());
        userDTO.setEmail(user1.getEmail());
        userDTO.setAddress(user1.getAddress());
        userDTO.setCreatedAt(user1.getCreatedAt());

        return userDTO;

    }

    public void deleteUserById(Long id) {
        this.userRepository.deleteById(id);
    }

    public UserDTO getUserById(Long id) throws IdInvalidException {
        User currentUser = this.userRepository.findById(id).orElseThrow(
                () -> new IdInvalidException("Id " + id + " khong ton tai !"));

        UserDTO dto = new UserDTO();
        dto.setId(currentUser.getId());
        dto.setName(currentUser.getName());
        dto.setGender(currentUser.getGender());
        dto.setAddress(currentUser.getAddress());
        dto.setEmail(currentUser.getEmail());
        dto.setCreatedAt(currentUser.getCreatedAt());
        dto.setUpdatedAt(currentUser.getUpdatedAt());

        return dto;
    }

    public ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable) {

        Page<User> pageUser = this.userRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();

        Meta mt = new Meta();

        mt.setPage(pageUser.getNumber() + 1);
        mt.setPageSize(pageUser.getSize());
        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());
        rs.setMeta(mt);

        List<User> users = pageUser.getContent();
        List<UserDTO> dtos = new ArrayList<>();
        for (User currentUser : users) {
            UserDTO dto = new UserDTO();
            dto.setId(currentUser.getId());
            dto.setName(currentUser.getName());
            dto.setGender(currentUser.getGender());
            dto.setAddress(currentUser.getAddress());
            dto.setEmail(currentUser.getEmail());
            dto.setCreatedAt(currentUser.getCreatedAt());
            dto.setUpdatedAt(currentUser.getUpdatedAt());
            dtos.add(dto);
        }
        rs.setResult(dtos);
        return rs;
    }

    public UpdateUserDTO updateUser(User reqUser) throws IdInvalidException {

        User currentUser = this.userRepository.findById(reqUser.getId()).orElseThrow(
                () -> new IdInvalidException("Id " + reqUser.getId() + " khong ton tai !")

        );

        currentUser.setAge(reqUser.getAge());
        currentUser.setName(reqUser.getName());
        currentUser.setGender(reqUser.getGender());
        currentUser.setAddress(reqUser.getAddress());

        // update
        currentUser = this.userRepository.save(currentUser);

        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setId(currentUser.getId());
        dto.setName(currentUser.getName());
        dto.setGender(currentUser.getGender());
        dto.setAddress(currentUser.getAddress());
        dto.setEmail(currentUser.getEmail());
        dto.setUpdatedAt(currentUser.getUpdatedAt());

        return dto;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }
}
