package ch.apg.sso.ui;

import ch.apg.sso.security.ApgSecurityUtils;
import ch.apg.sso.security.AuthorityConstant;
import ch.apg.sso.user.UserService;
import ch.apg.sso.user.dto.AccountDTO;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

@SpringComponent
@SpringView(name = HomeView.VIEW_NAME)
public class HomeView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "home";

    public static final String VIEW_TITLE = "Home View";

    @Autowired
    private UserService userService;

    @PostConstruct
    void init() {
        Label title = new Label(VIEW_TITLE);
        addComponent(title);

        if (!ApgSecurityUtils.isAuthenticated()) {
            addComponent(new Label("Momentan ist kein Benutzer angemeldet."));
            Button loginButton = new Button("anmelden");
            loginButton.addClickListener(e -> login());
            addComponent(loginButton);
        } else {
            AccountDTO accountDTO = userService.getCurrentAccount();
            addComponent(new Label(
                    String.format("Momentan ist der Benutzer %s mit den Berechtigungen %s angemeldet.",
                            accountDTO.getUsername(), accountDTO.getAuthorities().stream().map(Enum::name).collect(Collectors.joining(", ")))
            ));
            if (ApgSecurityUtils.hasCurrentUserAnyAuthority(AuthorityConstant.VERKAUF)) {
                addComponent(new Label("Da Sie die Rolle " + AuthorityConstant.VERKAUF.name() + " besitzen haben Zugriff auf weitere Funktionen."));
            }
            if (!ApgSecurityUtils.hasCurrentUserAnyAuthority(AuthorityConstant.VERKAUF)) {
                addComponent(new Label("Benutzer mit der Rolle " + AuthorityConstant.VERKAUF.name() + " (TU16 - TU21) haben Zugriff auf weitere Funktionen."));
            }
            Button logoutButton = new Button("abmelden");
            logoutButton.addClickListener(e -> logout());
            addComponent(logoutButton);
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // This view is constructed in the init() method()
    }

    private void login() {
        getUI().getPage().setLocation("/oauth2/authorization/keycloak");
    }

    private void logout() {
        getUI().getSession().close();
        getUI().getPage().setLocation("/logout");
    }
}
