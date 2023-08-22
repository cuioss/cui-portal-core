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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.Tag;

import de.cuioss.tools.string.MoreStrings;
import lombok.Getter;

/**
 * Builder for MicroProfile {@link MetricID}.
 */
@Getter
public class MetricIdBuilder {

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
    }

    /**
     * @param name the metrics name
     *
     * @return this builder
     */
    public MetricIdBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * @param cause that should be processed into {@link Tag}s.
     *
     * @return this builder
     */
    public MetricIdBuilder exception(final Throwable cause) {
        exception = cause;
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
        return this;
    }

    /**
     * @param tags to be added to the metrics tags.
     *
     * @return the {@link MetricIdBuilder} with the given Tags
     */
    public MetricIdBuilder tags(Tag[] tags) {
        if (null != tags) {
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
        tags.add(deepCopy(tag));
        return this;
    }

    private static Tag deepCopy(final Tag tag) {
        return new Tag(tag.getTagName(), tag.getTagValue());
    }

    /**
     * @return the create {@link MetricID}
     */
    public MetricID build() {
        checkArgument(!MoreStrings.isEmpty(name), "name must not be null nor empty");

        if (null != exception) {
            for (Function<Throwable, Tag> exceptionTagMapper : exceptionTagMappers) {
                Optional.ofNullable(exceptionTagMapper.apply(exception)).ifPresent(tags::add);
            }
        }

        return new MetricID(name, tags.toArray(new Tag[0]));
    }
}
