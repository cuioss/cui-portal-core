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
package de.cuioss.portal.metrics.utils;

import static de.cuioss.tools.base.Preconditions.checkArgument;

import de.cuioss.tools.logging.CuiLogger;
import lombok.Getter;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Builder for MicroProfile {@link MetricID}.
 */
@Getter
public class MetricIdBuilder {

    private static final CuiLogger LOGGER = new CuiLogger(MetricIdBuilder.class);

    private String name;

    private final List<Tag> tags;

    private Throwable exception;

    private final List<Function<Throwable, Tag>> exceptionTagMappers;

    /**
     * Default Constructor
     */
    public MetricIdBuilder() {
        tags = new ArrayList<>();
        exceptionTagMappers = new ArrayList<>();
        LOGGER.debug("Created new MetricIdBuilder");
    }

    /**
     * @param name the metrics name
     *
     * @return this builder
     */
    public MetricIdBuilder name(String name) {
        this.name = name;
        LOGGER.debug("Set metric name: %s", name);
        return this;
    }

    /**
     * @param cause that should be processed into {@link Tag}s.
     *
     * @return this builder
     */
    public MetricIdBuilder exception(final Throwable cause) {
        exception = cause;
        if (cause != null) {
            LOGGER.debug("Set exception: %s", cause.getClass().getName());
        }
        return this;
    }

    /**
     * @param mapper a mapper to process the given {@link #exception(Throwable)}
     *               into a {@link Tag}.
     *
     * @return this builder
     */
    public MetricIdBuilder exceptionTagMapper(final Function<Throwable, Tag> mapper) {
        exceptionTagMappers.add(mapper);
        LOGGER.debug("Added exception tag mapper, total mappers: %s", exceptionTagMappers.size());
        return this;
    }

    /**
     * @param tags to be added to the metrics tags.
     *
     * @return the {@link MetricIdBuilder} with the given Tags
     */
    public MetricIdBuilder tags(Tag[] tags) {
        if (null != tags) {
            LOGGER.debug("Adding %s tags", tags.length);
            for (Tag tag : tags) {
                if (null != tag) {
                    this.tags.add(deepCopy(tag));
                }
            }
        }
        return this;
    }

    /**
     * @param tag to be added to the metrics tags.
     *
     * @return the {@link MetricIdBuilder} with the given Tag
     */
    public MetricIdBuilder tag(Tag tag) {
        if (null != tag) {
            tags.add(deepCopy(tag));
            LOGGER.debug("Added tag: %s=%s", tag.getTagName(), tag.getTagValue());
        }
        return this;
    }

    /**
     * @return the create {@link MetricID}
     */
    public MetricID build() {
        LOGGER.debug("Building MetricID");
        checkArgument(null != name && !name.trim().isEmpty(), "name must be set");

        if (null != exception) {
            LOGGER.debug("Processing %s exception tag mappers", exceptionTagMappers.size());
            exceptionTagMappers.stream()
                    .map(mapper -> mapper.apply(exception))
                    .filter(tag -> tag != null)
                    .forEach(tags::add);
        }

        var metricId = new MetricID(name, tags.toArray(new Tag[0]));
        LOGGER.debug("Built MetricID '%s' with %s tags", name, tags.size());
        return metricId;
    }

    private static Tag deepCopy(final Tag tag) {
        return new Tag(tag.getTagName(), tag.getTagValue());
    }
}
