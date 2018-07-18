package org.blogsite.youngsoft.tableview.listeners;

import org.blogsite.youngsoft.tableview.SortingStatus;

/**
 * {@link SortingStatus}가 변경되었을 때에 통지를받는 리스너 정의.
 */
public interface SortingStatusChangeListener {

    /**
     * {@link SortingStatus}가 변경되었을 때에 불려가는 콜백 메소드.
     *
     * @param newSortingStatus The new {@link SortingStatus}.
     */
    void onSortingStatusChanged(final SortingStatus newSortingStatus);
}
