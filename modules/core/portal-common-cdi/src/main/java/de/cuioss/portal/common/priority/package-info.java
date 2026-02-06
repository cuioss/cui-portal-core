/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Provides utility classes for handling priority-based ordering in Portal applications.
 *
 * <h2>Overview</h2>
 * This package contains classes for managing and ordering dynamic elements and alternatives
 * based on the {@link jakarta.annotation.Priority} annotation. It enables consistent
 * and flexible ordering of components, services, and configurations across the Portal
 * framework.
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.common.priority.PortalPriorities} - Core utility class
 *       providing static methods for priority-based sorting and comparison</li>
 *   <li>{@link de.cuioss.portal.common.priority.PriorityComparator} - Reusable comparator
 *       implementation for ordering elements by their priority annotations</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 * 
 * <h3>Basic Sorting</h3>
 * <pre>
 * // Sort a list of beans by their priority annotation
 * List&lt;MyBean&gt; beans = new ArrayList&lt;&gt;();
 * List&lt;MyBean&gt; sortedBeans = PortalPriorities.sortByPriority(beans);
 * </pre>
 * 
 * <h3>Custom Comparator Usage</h3>
 * <pre>
 * // Create a priority-based comparator
 * PriorityComparator&lt;MyBean&gt; comparator = new PriorityComparator&lt;&gt;();
 * 
 * // Use with Java collections
 * Collections.sort(beans, comparator);
 * TreeSet&lt;MyBean&gt; orderedSet = new TreeSet&lt;&gt;(comparator);
 * </pre>
 *
 * <h2>Priority Guidelines</h2>
 * <ul>
 *   <li>Use {@link jakarta.annotation.Priority} to define component ordering</li>
 *   <li>Higher priority values (higher numbers) indicate higher precedence</li>
 *   <li>Elements without priority annotation default to priority 0</li>
 *   <li>Reserved ranges:
 *     <ul>
 *       <li>0-99: Framework core components</li>
 *       <li>100-199: Standard extensions</li>
 *       <li>200+: Application-specific components</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * <h2>Thread Safety</h2>
 * All components in this package are immutable and thread-safe. They can be safely
 * used in concurrent environments and shared across multiple threads.
 *
 * @author Oliver Wolff
 * @see jakarta.annotation.Priority
 * @see java.util.Comparator
 */
package de.cuioss.portal.common.priority;
