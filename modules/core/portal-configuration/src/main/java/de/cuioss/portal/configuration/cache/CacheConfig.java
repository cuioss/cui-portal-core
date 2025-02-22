/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.configuration.cache;

import de.cuioss.portal.configuration.types.ConfigAsCacheConfig;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Runtime representation for simple CacheConfig, generated by
 * {@link ConfigAsCacheConfig}
 *
 * @author Oliver Wolff
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class CacheConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -9171609866171876392L;

    /** The config key suffix for the {@link #expiration} property */
    public static final String EXPIRATION_KEY = "expiration";

    /** The config key suffix for the {@link #size} property */
    public static final String SIZE_KEY = "size";

    /** The config key suffix for the {@link #timeUnit} property */
    public static final String EXPIRATION_UNIT_KEY = "expiration_unit";

    /** The config key suffix for the {@link #recordStatistics} property */
    public static final String RECORD_STATISTICS_KEY = "record_statistics";

    /**
     * The expiration for the cache, the unit is defined at {@link #getTimeUnit()}.
     */
    @Getter
    private final long expiration;

    @Getter
    private final TimeUnit timeUnit;

    /** The size / count for the objects to be caches. */
    @Getter
    private final long size;

    /**
     * Indicates whether the cache should record statistics. The default producer
     * will use the values of
     * {@link de.cuioss.portal.configuration.MetricsConfigKeys#PORTAL_METRICS_ENABLED}
     * in order to set this configuration. It is assumed the recordStatistics are
     * read only in the context of metrics.
     */
    @Getter
    private final boolean recordStatistics;
}
