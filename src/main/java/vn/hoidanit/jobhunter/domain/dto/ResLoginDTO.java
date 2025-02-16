package vn.hoidanit.jobhunter.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResLoginDTO {
    private String accessToken;
    private InnerResLoginDTO userLogin;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InnerResLoginDTO {
        private long id;
        private String name;
        private String email;

    }

}
