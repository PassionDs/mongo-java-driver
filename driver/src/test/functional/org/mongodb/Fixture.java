/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
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

package org.mongodb;

import org.mongodb.impl.MongoClientImpl;
import org.mongodb.impl.MongoClientsImpl;

import java.net.UnknownHostException;

/**
 * Helper class for the acceptance tests.  Considering replacing with MongoClientTestBase.
 */
public final class Fixture {
    public static final String DEFAULT_URI = "mongodb://localhost:27017";
    public static final String MONGODB_URI_SYSTEM_PROPERTY_NAME = "org.mongodb.test.uri";

    private static MongoClientImpl mongoClient;

    private Fixture() {
    }

    public static synchronized MongoClient getMongoClient() {
        if (mongoClient == null) {
            final String mongoURIProperty = System.getProperty(MONGODB_URI_SYSTEM_PROPERTY_NAME);
            final String mongoURIString = mongoURIProperty == null || mongoURIProperty.isEmpty()
                                          ? DEFAULT_URI : mongoURIProperty;
            try {
                mongoClient = MongoClientsImpl.create(new MongoClientURI(mongoURIString));
            } catch (UnknownHostException e) {
                throw new IllegalArgumentException("Invalid Mongo URI: " + mongoURIString, e);
            }
        }
        return mongoClient;
    }


    public static MongoConnection getMongoConnection() {
        return mongoClient.getConnection();
    }

    // Note this is not safe for concurrent access - if you run multiple tests in parallel from the same class,
    // you'll drop the DB
    public static MongoDatabase getCleanDatabaseForTest(final Class<?> testClass) {
        final MongoDatabase database = getMongoClient().getDatabase(testClass.getSimpleName());

        database.tools().drop();
        return database;
    }
}
