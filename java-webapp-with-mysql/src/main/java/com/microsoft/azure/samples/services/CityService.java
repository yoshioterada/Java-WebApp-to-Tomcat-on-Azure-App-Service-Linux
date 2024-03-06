/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */
package com.microsoft.azure.samples.services;

import com.microsoft.azure.samples.utils.EntityManagerUtil;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.microsoft.azure.samples.entities.City;
import com.microsoft.azure.samples.entities.Country;

import java.util.List;

@RequestScoped
public class CityService {

    public List<City> findOver1MillPopulation(String countrycode) {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        TypedQuery<City> query = entityManager.createNamedQuery("City.findOver1MillPopulation", City.class);
        query.setParameter("countrycode", countrycode);
        return query.getResultList();
    }

    public List<String> findAllContinents() {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        TypedQuery<String> query = entityManager.createNamedQuery("Country.findAllContinent", String.class);
        return query.getResultList();
    }

    public List<Country> findItemByContinent(String continent) {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        TypedQuery<Country> query = entityManager.createNamedQuery("Country.findByContinent", Country.class);
        query.setParameter("continent", continent);
        return query.getResultList();
    }
}