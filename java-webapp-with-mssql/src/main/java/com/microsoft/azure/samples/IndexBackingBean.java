/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */
package com.microsoft.azure.samples;

import com.microsoft.azure.samples.services.CityService;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import javax.faces.view.ViewScoped;
import com.microsoft.azure.samples.entities.City;
import com.microsoft.azure.samples.entities.Country;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.primefaces.event.MenuActionEvent;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuModel;

@Named("countryBackingBean")
@ViewScoped
public class IndexBackingBean implements Serializable {

    @Inject
    CityService citySvc;

    private List<City> city;
    private String countrycode;
    private MenuModel model;

    public List<City> getCity() {
        return citySvc.findOver1MillPopulation(countrycode);
    }

    public MenuModel getModel() {
        return model;
    }

    @PostConstruct
    public void init() {
        model = new DefaultMenuModel();

        //First submenu        
        DefaultSubMenu firstSubmenu = new DefaultSubMenu();
        firstSubmenu.setLabel("Select Country");

        getAllContinents().stream().forEach(continents -> {
            DefaultSubMenu submenue = DefaultSubMenu
                    .builder()
                    .id(continents)
                    .expanded(true)
                    .label(continents)
                    .icon("pi pi-home")
                    .elements(createSecondMenue(continents))
                    .build();
            firstSubmenu.getElements().add(submenue);
        });
        model.getElements().add(firstSubmenu);

    }

    private List<MenuElement> createSecondMenue(String continent) {
        List<Country> countryInContinents = getCountryInContinents(continent);
        return countryInContinents.stream().map((var country) -> {
            String countryName = country.getName();
            countrycode = country.getCode();
            Map<String, List<String>> param = new HashMap<>();
            param.put("countrycode", Arrays.asList(countrycode));

            return DefaultMenuItem
                    .builder()
                    .ajax(true)
                    .command("#{countryBackingBean.selectMenueOfCountry}")
                    .update(":mainform:citydata")
                    .value(countryName)
                    .params(param)
                    .build();
        }).collect(Collectors.toList());
    }

    public List<String> getAllContinents() {
        return citySvc.findAllContinents();
    }

    public List<Country> getCountryInContinents(String continent) {
        return citySvc.findItemByContinent(continent);
    }

    public void selectMenueOfCountry(MenuActionEvent event) {
        countrycode = event.getMenuItem().getParams().get("countrycode").get(0);
    }
}
