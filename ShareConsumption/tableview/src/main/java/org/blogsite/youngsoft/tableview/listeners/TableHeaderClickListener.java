package org.blogsite.youngsoft.tableview.listeners;

/**
 * {@link org.blogsite.youngsoft.tableview.SortableTableView}의 테이블 헤더의 클릭을 대기하는 리스너 인터페이스.
 *
 */
public interface TableHeaderClickListener {

    /**
     * 이 메서드는 테이블 헤더를 클릭 할 때 호출
     *
     * @param columnIndex 클릭 된 열의 인덱스
     */
    void onHeaderClicked(final int columnIndex);

}
