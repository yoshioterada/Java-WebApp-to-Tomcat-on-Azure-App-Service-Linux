/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */
package com.microsoft.azure.samples.utils;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

// More useful implementation is following
// https://www.linkedin.com/pulse/getting-advantage-java-8-using-eclipselink-tomcat-se-nicola-cogotti/

@ApplicationScoped
public class EntityManagerUtil {

    private static final String PU_NAME = "JPAWorldDatasourcePU";
    @PersistenceUnit(unitName = PU_NAME)
    private static final EntityManagerFactory entityManagerFactory;

    static {
        Map<String, String> systemEnv = System.getenv();
        Map<String, Object> overwrite = new HashMap<>();

        systemEnv.forEach((var key, var value) -> {
            switch (key) {
                case "JDBC_DRIVER":
                    overwrite.put("toplink.jdbc.driver", value);
                    overwrite.put("javax.persistence.jdbc.driver", value);
                    break;
                case "JDBC_URL":
                    overwrite.put("toplink.jdbc.url", value);
                    overwrite.put("javax.persistence.jdbc.url", value);
                    break;
                case "DB_USER":
                    overwrite.put("toplink.jdbc.user", value);
                    overwrite.put("javax.persistence.jdbc.user", value);
                    break;
                case "DB_PASSWORD":
                    overwrite.put("toplink.jdbc.password", value);
                    overwrite.put("javax.persistence.jdbc.password", value);
                    break;
                default:
                    break;
            }
        });

        try {
            entityManagerFactory = Persistence.createEntityManagerFactory(PU_NAME, overwrite);
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public static void closeEntityManagerConnection() {
        entityManagerFactory.close();
    }
}
