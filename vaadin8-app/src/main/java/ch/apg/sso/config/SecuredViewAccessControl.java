/*
 * Copyright 2015 The original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.apg.sso.config;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.access.annotation.Secured;

import com.vaadin.spring.access.ViewAccessControl;
import com.vaadin.ui.UI;

import ch.apg.sso.security.ApgSecurityUtils;
import ch.apg.sso.security.AuthorityConstant;

/**
 * Implementation of {@link ViewAccessControl} that
 * checks if a view has the {@link org.springframework.security.access.annotation.Secured} annotation
 * and checks if the current user is authorized to access the view.
 */
public class SecuredViewAccessControl implements ApplicationContextAware, ViewAccessControl {
    private static final Logger log = LoggerFactory.getLogger(SecuredViewAccessControl.class);

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Check assess for given beanName
     */
    @Override
    public boolean isAccessGranted(UI ui, String beanName) {
        final Secured viewSecured = applicationContext.findAnnotationOnBean(beanName, Secured.class);

        if (viewSecured == null) {
            log.debug("No @Secured annotation found on view {}. Granting access.", beanName);
            return true;
        } else {
            final boolean result =
                ApgSecurityUtils.hasCurrentUserAnyAuthority(Stream.of(viewSecured.value()).map(AuthorityConstant::valueOf).collect(Collectors.toList()).toArray(new AuthorityConstant[]{}));
            log.debug("Access granted to view bean {}: {}", beanName, result);
            return result;
        }
    }
}
