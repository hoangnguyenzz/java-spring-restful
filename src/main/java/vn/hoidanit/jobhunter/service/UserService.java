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
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResCreateUserDTO createUser(User user) throws IdInvalidException {

        if (this.userRepository.existsByEmail(user.getEmail())) {
            throw new IdInvalidException("Email " + user.getEmail() + " đã tồn tại !");
        }

        User user1 = this.userRepository.save(user);

        ResCreateUserDTO userDTO = new ResCreateUserDTO();

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

    public ResUserDTO getUserById(Long id) throws IdInvalidException {
        User currentUser = this.userRepository.findById(id).orElseThrow(
                () -> new IdInvalidException("Id " + id + " khong ton tai !"));

        ResUserDTO dto = new ResUserDTO();
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

        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageUser.getNumber() + 1);
        mt.setPageSize(pageUser.getSize());
        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());
        rs.setMeta(mt);

        List<User> users = pageUser.getContent();
        List<ResUserDTO> dtos = new ArrayList<>();
        for (User currentUser : users) {
            ResUserDTO dto = new ResUserDTO();
            dto.setId(currentUser.getId());
            dto.setName(currentUser.getName());
            dto.setGender(currentUser.getGender());
            dto.setAddress(currentUser.getAddress());
            dto.setEmail(currentUser.getEmail());
            dto.setAge(currentUser.getAge());
            dto.setCreatedAt(currentUser.getCreatedAt());
            dto.setUpdatedAt(currentUser.getUpdatedAt());
            dtos.add(dto);
        }
        rs.setResult(dtos);
        return rs;
    }

    public ResUpdateUserDTO updateUser(User reqUser) throws IdInvalidException {

        User currentUser = this.userRepository.findById(reqUser.getId()).orElseThrow(
                () -> new IdInvalidException("Id " + reqUser.getId() + " khong ton tai !")

        );

        currentUser.setAge(reqUser.getAge());
        currentUser.setName(reqUser.getName());
        currentUser.setGender(reqUser.getGender());
        currentUser.setAddress(reqUser.getAddress());

        // update
        currentUser = this.userRepository.save(currentUser);

        ResUpdateUserDTO dto = new ResUpdateUserDTO();
        dto.setId(currentUser.getId());
        dto.setName(currentUser.getName());
        dto.setGender(currentUser.getGender());
        dto.setAddress(currentUser.getAddress());
        dto.setEmail(currentUser.getEmail());
        dto.setUpdatedAt(currentUser.getUpdatedAt());

        return dto;
    }

    public void logout(User user) {
        user.setRefreshToken(null);
        this.userRepository.save(user);
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public void updateRefreshToken(String refresh_token, String email) {
        User user = this.handleGetUserByUsername(email);
        {
            if (user != null) {
                user.setRefreshToken(refresh_token);
                this.userRepository.save(user);
            }
        }
    }

    public User fetchUserByRefreshTokenAndEmail(String refresh_token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(refresh_token, email);
    }

    public User getCurrentUserWithToken() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        if (email.equals("")) {
            throw new IdInvalidException("Access Token không hợp lệ !");
        }
        User currentUser = handleGetUserByUsername(email);
        return currentUser;

    }
}
