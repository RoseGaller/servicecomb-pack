/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.servicecomb.pack.alpha.fsm.spring.integration.akka;

import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

import java.util.*;
import org.springframework.core.env.StandardEnvironment;

public class AkkaConfigPropertyAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  public static final String PROPERTY_SOURCE_NAME = "akkaConfig.";

  public static Map<String, Object> getPropertyMap(ConfigurableEnvironment environment) {
    final Map<String, Object> propertyMap = new HashMap<>();

    for (final PropertySource source : environment.getPropertySources()) {
      if (isEligiblePropertySource(source)) {
        final EnumerablePropertySource enumerable = (EnumerablePropertySource) source;
        LOG.debug("Adding properties from property source " + source.getName());
        for (final String name : enumerable.getPropertyNames()) {
          if (name.startsWith(PROPERTY_SOURCE_NAME) && !propertyMap.containsKey(name)) {
            String key = name.substring(PROPERTY_SOURCE_NAME.length());
            Object value = environment.getProperty(name);
            if (LOG.isTraceEnabled()) {
              LOG.trace("Adding property {}={}" + key, value);
            }
            propertyMap.put(key, value);
          }
        }
      }
    }

    return Collections.unmodifiableMap(propertyMap);
  }

  public static boolean isEligiblePropertySource(PropertySource source) {
    // Exclude system environment properties and system property sources
    // because they are already included in the default configuration
    final String name = source.getName();
    return (source instanceof EnumerablePropertySource) &&
        !(
            name.equalsIgnoreCase(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME) ||
                name.equalsIgnoreCase(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME)
        );
  }
}
