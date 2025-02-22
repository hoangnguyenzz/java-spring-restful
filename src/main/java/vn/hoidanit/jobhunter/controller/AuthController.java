package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.LoginDTO;
import vn.hoidanit.jobhunter.domain.dto.ResLoginDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

        private AuthenticationManagerBuilder authenticationManagerBuilder;

        private SecurityUtil securityUtil;
        private UserService userService;

        @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
        private long refreshTokenExpiration;

        public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
                        UserService userService) {
                this.authenticationManagerBuilder = authenticationManagerBuilder;
                this.securityUtil = securityUtil;
                this.userService = userService;
        }

        @PostMapping("/login")
        public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
                // Nạp input gồm username/password vào Security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                loginDTO.getUsername(), loginDTO.getPassword());

                // xác thực người dùng => cần viết hàm loadUserByUsername
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);
                // Xét thông tin người dùng đăng nhập vào context(có thể sử dụng sau này :)
                SecurityContextHolder.getContext().setAuthentication(authentication);

                ResLoginDTO res = new ResLoginDTO();

                User currentUser = this.userService.handleGetUserByUsername(loginDTO.getUsername());
                if (currentUser != null) {
                        ResLoginDTO.InnerResLoginDTO dto = new ResLoginDTO.InnerResLoginDTO(currentUser.getId(),
                                        currentUser.getName(), currentUser.getEmail());

                        res.setUser(dto);
                }
                // access token
                String access_token = this.securityUtil.createAccessToken(authentication.getName(), res.getUser());
                // refresh token
                String refresh_token = this.securityUtil.createRefreshToken(currentUser.getEmail(), res);

                this.userService.updateRefreshToken(refresh_token, currentUser.getEmail());

                res.setAccessToken(access_token);

                // set cookies
                ResponseCookie resCookies = ResponseCookie
                                .from("refresh_token", refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(res);
        }

        @GetMapping("/account")
        public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() throws IdInvalidException {
                User currentUser = this.userService.getCurrentUserWithToken();
                ResLoginDTO.InnerResLoginDTO dto = new ResLoginDTO.InnerResLoginDTO();
                ResLoginDTO.UserGetAccount res = new ResLoginDTO.UserGetAccount();
                if (currentUser != null) {
                        dto.setId(currentUser.getId());
                        dto.setName(currentUser.getName());
                        dto.setEmail(currentUser.getEmail());
                        res.setUser(dto);
                }

                return ResponseEntity.ok().body(res);
        }

        @GetMapping("/refresh")
        public ResponseEntity<ResLoginDTO> getRefreshToken(
                        @CookieValue(name = "refresh_token") String refresh_token) throws IdInvalidException {

                Jwt jwt = this.securityUtil.checkValidRefreshToken(refresh_token);
                String email = jwt.getSubject();
                User currentUser = this.userService.fetchUserByRefreshTokenAndEmail(refresh_token, email);
                if (currentUser == null) {
                        throw new IdInvalidException("Refresh token khong hop le !");
                }

                ResLoginDTO res = new ResLoginDTO();

                if (currentUser != null) {
                        ResLoginDTO.InnerResLoginDTO dto = new ResLoginDTO.InnerResLoginDTO(currentUser.getId(),
                                        currentUser.getName(), currentUser.getEmail());

                        res.setUser(dto);
                }
                // access token
                String access_token = this.securityUtil.createAccessToken(currentUser.getEmail(), res.getUser());
                // refresh token
                String new_refresh_token = this.securityUtil.createRefreshToken(currentUser.getEmail(), res);

                this.userService.updateRefreshToken(new_refresh_token, currentUser.getEmail());

                res.setAccessToken(access_token);

                // set cookies
                ResponseCookie resCookies = ResponseCookie
                                .from("refresh_token", new_refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(res);
        }

        @PostMapping("/logout")
        public ResponseEntity<Void> logout() throws IdInvalidException {
                User currentUser = this.userService.getCurrentUserWithToken();
                this.userService.logout(currentUser);
                // set cookies
                ResponseCookie resCookies = ResponseCookie
                                .from("refresh_token", null)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(null);
        }
}
