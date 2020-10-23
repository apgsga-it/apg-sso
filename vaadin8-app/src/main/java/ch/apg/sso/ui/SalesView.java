package ch.apg.sso.ui;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import ch.apg.sso.configuration.ConfigurationService;
import ch.apg.sso.configuration.dto.SalesConfigurationDTO;
import ch.apg.sso.security.AuthorityConstant;
import ch.apg.sso.security.AuthorityConstantStrings;

@SpringComponent
@SpringView(name = SalesView.VIEW_NAME)
@Secured({AuthorityConstantStrings.VERKAUF})
public class SalesView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "sales";

    public static final String VIEW_TITLE = "Sales View";

    @Autowired
    private ConfigurationService configurationService;

    @PostConstruct
    void init() {
        Label title = new Label(VIEW_TITLE);
        addComponent(title);

        addComponent(new Label("Nur Benutzer mit der Rolle '" + AuthorityConstant.VERKAUF.name() + "' haben Zugriff auf diese Seite."));

        addComponent(new Label("Der Server hat folgende Daten zurückgegeben."));
        SalesConfigurationDTO salesConfigurationDTO = configurationService.getSalesConfiguration();
        Label c = new Label(salesConfigurationDTO.toString());
        c.setSizeFull();
		addComponent(c);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // This view is constructed in the init() method()
    }
}
