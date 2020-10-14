package ch.apg.sso.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import ch.apg.sso.configuration.ConfigurationService;
import ch.apg.sso.configuration.dto.ConfigurationDTO;
import ch.apg.sso.security.ApgSecurityUtils;
import ch.apg.sso.security.AuthorityConstant;

@Theme("valo")
@SpringUI
@SpringViewDisplay
public class Vaadin8UI extends UI implements ViewDisplay {
    private Panel springViewDisplay;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        setContent(root);

        final CssLayout navigationBar = new CssLayout();
        navigationBar.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        navigationBar.addComponent(createNavigationButton(HomeView.VIEW_TITLE, HomeView.VIEW_NAME));

        if (ApgSecurityUtils.hasCurrentUserAnyAuthority(AuthorityConstant.VERKAUF)) {
            navigationBar.addComponent(createNavigationButton(SalesView.VIEW_TITLE, SalesView.VIEW_NAME));
        }

        root.addComponent(navigationBar);

        springViewDisplay = new Panel();
        springViewDisplay.setSizeFull();
        root.addComponent(springViewDisplay);
        root.setExpandRatio(springViewDisplay, 1.0f);

        if (ApgSecurityUtils.isAuthenticated()) {
            ConfigurationDTO configurationDTO = configurationService.getConfiguration();
            root.addComponent(new Label("Version " + configurationDTO.getVersion()));
        }

        getUI().getNavigator().navigateTo(HomeView.VIEW_NAME);
    }

    private Button createNavigationButton(String caption, final String viewName) {
        Button button = new Button(caption);
        button.addStyleName(ValoTheme.BUTTON_SMALL);
        button.addClickListener(event -> getUI().getNavigator().navigateTo(viewName));
        return button;
    }

    @Override
    public void showView(View view) {
        springViewDisplay.setContent((Component) view);
    }
}
