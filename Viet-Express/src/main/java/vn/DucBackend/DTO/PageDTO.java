package vn.DucBackend.DTO;

import java.util.List;

/**
 * Generic DTO for paginated results.
 * Contains the page content along with pagination metadata.
 * 
 * @param <T> The type of items in the page
 */
public class PageDTO<T> {

    private List<T> content;
    private int currentPage;
    private int totalPages;
    private int totalItems;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;

    public PageDTO() {
    }

    public PageDTO(List<T> content, int currentPage, int totalPages, int totalItems, int pageSize) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
        this.pageSize = pageSize;
        this.hasNext = currentPage < totalPages - 1;
        this.hasPrevious = currentPage > 0;
    }

    // Static factory method for convenience
    public static <T> PageDTO<T> of(List<T> allItems, List<T> pageContent, int currentPage, int pageSize) {
        int totalItems = allItems.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        return new PageDTO<>(pageContent, currentPage, totalPages, totalItems, pageSize);
    }

    // Getters and Setters
    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
}
