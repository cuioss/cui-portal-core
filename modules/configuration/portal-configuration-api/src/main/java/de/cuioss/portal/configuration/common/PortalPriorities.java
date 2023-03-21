package de.cuioss.portal.configuration.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Priority;

import de.cuioss.portal.configuration.bundles.ResourceBundleLocator;
import lombok.experimental.UtilityClass;

/**
 * <p>
 * Provides constants and methods for ordering portal related elements.
 * </p>
 * <h2>Design</h2>
 * <p>
 * Some items at the portal need an explicit ordering in order to check the priority. For e.g.
 * instances of {@link ResourceBundleLocator} can have a higher priority for overwriting portal
 * defined messages. The ordering relies on the {@link Priority} annotation on the corresponding
 * elements. The higher the {@link Priority#value()} the higher is the priority. If the
 * corresponding element has not annotated with {@link Priority} the order will be defaulted to
 * {@link #DEFAULT_LEVEL}
 * </p>
 * <h2>Implementation</h2>
 * <p>
 * In order to have a better structure this class provides some constants defining the general range
 * of portal applications. In addition there is the helper method {@link #sortByPriority(List)}
 * simplifying the task of sorting.
 * </p>
 *
 * @author Oliver Wolff
 */
@UtilityClass
public final class PortalPriorities {

    /**
     * Specifies the priority / ordering for the object having no {@link Priority} annotation,
     * integer value will be 0;
     */
    public static final int DEFAULT_LEVEL = 0;

    /**
     * Specifies the priority / ordering for the core, integer value will be 10;
     */
    public static final int PORTAL_CORE_LEVEL = 10;

    /**
     * Specifies the priority / ordering for a concrete module that will override
     * {@link #PORTAL_CORE_LEVEL}, integer value will be 50;
     */
    public static final int PORTAL_MODULE_LEVEL = 50;

    /**
     * Specifies the priority / ordering for an assembly that will override
     * {@link #PORTAL_MODULE_LEVEL}, integer value will be 100;
     */
    public static final int PORTAL_ASSEMBLY_LEVEL = 100;

    /**
     * Specifies the priority / ordering for an installation-specific priority that will override
     * {@link #PORTAL_ASSEMBLY_LEVEL}, integer value will be 150;
     */
    public static final int PORTAL_INSTALLATION_LEVEL = 150;

    /**
     * Helper method that sorts a number of given objects regarding to their priority. The Priority
     * is assumed to be defined by the {@link Priority} annotation at class level. It it is not set
     * the priority is defaulted to {@value #DEFAULT_LEVEL}
     *
     * @param toBeSorted must not be null
     * @return the sorted list. In case of {@link List#size()} is lower than 2 the original list
     *         will be returned.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> sortByPriority(final List<T> toBeSorted) {
        if (toBeSorted.size() < 2) {
            return toBeSorted;
        }
        final List<PriorityComparator> wrapperList = new ArrayList<>();
        for (final T element : toBeSorted) {
            wrapperList.add(new PriorityComparator(element));
        }
        Collections.sort(wrapperList);
        final List<T> result = new ArrayList<>();
        for (final PriorityComparator comparator : wrapperList) {
            result.add((T) comparator.getWrapped());
        }
        return result;
    }
}
