package vn.DucBackend.Utils;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * Utility class for pagination operations.
 * Provides methods to paginate lists with configurable page size.
 */
public class PaginationUtil {

    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * DTO class for pagination result - used in templates
     */
    @Getter
    @AllArgsConstructor
    public static class PageDTO<T> {
        private List<T> content;
        private int currentPage;
        private int totalPages;
        private int totalItems;
        private boolean hasPrevious;
        private boolean hasNext;
    }

    /**
     * Paginates a list and returns a PageDTO with all pagination info.
     * Page is 1-indexed (first page = 1).
     */
    public static <T> PageDTO<T> paginate(List<T> items, int page, int pageSize) {
        if (items == null || items.isEmpty()) {
            return new PageDTO<>(Collections.emptyList(), 1, 0, 0, false, false);
        }

        int totalItems = items.size();
        int totalPages = getTotalPages(totalItems, pageSize);

        // Normalize page (1-indexed)
        int normalizedPage = Math.max(1, Math.min(page, totalPages));

        int fromIndex = (normalizedPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalItems);

        List<T> content = items.subList(fromIndex, toIndex);
        boolean hasPrevious = normalizedPage > 1;
        boolean hasNext = normalizedPage < totalPages;

        return new PageDTO<>(content, normalizedPage, totalPages, totalItems, hasPrevious, hasNext);
    }

    /**
     * Paginates a list with default page size (10).
     */
    public static <T> PageDTO<T> paginate(List<T> items, int page) {
        return paginate(items, page, DEFAULT_PAGE_SIZE);
    }

    /**
     * Calculates total pages for a given list size and page size.
     */
    public static int getTotalPages(int totalItems, int pageSize) {
        if (totalItems <= 0 || pageSize <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalItems / pageSize);
    }

    /**
     * Calculates total pages with default page size.
     */
    public static int getTotalPages(int totalItems) {
        return getTotalPages(totalItems, DEFAULT_PAGE_SIZE);
    }

    /**
     * Validates and normalizes page number.
     * Returns 0 if page is negative, or last valid page if exceeds total.
     */
    public static int normalizePage(int page, int totalPages) {
        if (page < 0)
            return 0;
        if (totalPages <= 0)
            return 0;
        if (page >= totalPages)
            return totalPages - 1;
        return page;
    }
}
