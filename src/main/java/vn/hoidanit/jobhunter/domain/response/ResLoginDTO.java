package vn.hoidanit.jobhunter.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.domain.Role;

@Getter
@Setter
public class ResLoginDTO {
    @JsonProperty("access_token")
    private String accessToken;
    private InnerResLoginDTO user;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InnerResLoginDTO {
        private long id;
        private String name;
        private String email;
        private Role role;

    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserGetAccount {
        private InnerResLoginDTO user;

    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInsideToken {
        private long id;
        private String name;
        private String email;
    }

}
