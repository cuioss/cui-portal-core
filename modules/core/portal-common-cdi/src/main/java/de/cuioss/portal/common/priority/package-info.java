/**
 * Provides utility classes for handling priority-based ordering in Portal applications.
 *
 * <h2>Overview</h2>
 * This package contains classes for managing and ordering dynamic elements and alternatives
 * based on the {@link jakarta.annotation.Priority} annotation.
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.common.priority.PortalPriorities} - Core utility class for sorting elements by priority</li>
 *   <li>{@link de.cuioss.portal.common.priority.PriorityComparator} - Comparator implementation for priority-based sorting</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>
 * // Sort a list of beans by their priority annotation
 * List<MyBean> beans = new ArrayList<>();
 * List<MyBean> sortedBeans = PortalPriorities.sortByPriority(beans);
 * </pre>
 *
 * <h2>Best Practices</h2>
 * <ul>
 *   <li>Always use {@link jakarta.annotation.Priority} for elements that need to be ordered</li>
 *   <li>Higher priority values indicate higher precedence</li>
 *   <li>Elements without priority annotation default to priority 0</li>
 * </ul>
 *
 * @author Oliver Wolff
 */
package de.cuioss.portal.common.priority;
