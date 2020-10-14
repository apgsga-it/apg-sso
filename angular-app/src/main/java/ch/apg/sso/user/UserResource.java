package ch.apg.sso.user;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.apg.sso.user.dto.AccountDTO;

@RestController
@RequestMapping("/api")
public class UserResource {
    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/account")
    public AccountDTO getAccount() {
        return userService.getCurrentAccount();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, @AuthenticationPrincipal(expression = "idToken") OidcIdToken idToken) {
        return this.userService.logout(request, idToken);
    }
}
