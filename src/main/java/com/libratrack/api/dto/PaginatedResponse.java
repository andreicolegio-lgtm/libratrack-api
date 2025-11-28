package com.libratrack.api.dto;

import java.util.List;

/**
 * Generic DTO for paginated responses.
 *
 * @param <T> The type of content in the response.
 */
public class PaginatedResponse<T> {

  private List<T> content;
  private int totalPages;
  private long totalElements;
  private boolean isLast;
  private int pageNumber;
  private int pageSize;

  public PaginatedResponse(
      List<T> content,
      int totalPages,
      long totalElements,
      boolean isLast,
      int pageNumber,
      int pageSize) {
    this.content = content;
    this.totalPages = totalPages;
    this.totalElements = totalElements;
    this.isLast = isLast;
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
  }

  public List<T> getContent() {
    return content;
  }

  public void setContent(List<T> content) {
    this.content = content;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }

  public long getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(long totalElements) {
    this.totalElements = totalElements;
  }

  public boolean isLast() {
    return isLast;
  }

  public void setLast(boolean last) {
    isLast = last;
  }

  public int getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(int pageNumber) {
    this.pageNumber = pageNumber;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }
}
