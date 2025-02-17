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
 * List<MyBean> beans = new ArrayList<>();
 * List<MyBean> sortedBeans = PortalPriorities.sortByPriority(beans);
 * </pre>
 * 
 * <h3>Custom Comparator Usage</h3>
 * <pre>
 * // Create a priority-based comparator
 * PriorityComparator<MyBean> comparator = new PriorityComparator<>();
 * 
 * // Use with Java collections
 * Collections.sort(beans, comparator);
 * TreeSet<MyBean> orderedSet = new TreeSet<>(comparator);
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
