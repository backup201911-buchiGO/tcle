package org.blogsite.youngsoft.tableview.listeners;

/**
 * 리스너를 새로 고치는 스 와이프 정의
 *
 */
public interface SwipeToRefreshListener {

    /**
     * 사용자가 새로 고침을 트리거 할 때 콜백 메서드
     *
     * @param refreshIndicator 사용자에게 표시되는 새로 고침 표시기
     */
    void onRefresh(final RefreshIndicator refreshIndicator);

    /**
     * 사용자에게 표시되는 새로 고침 표시기를 나타내는 인터페이스
     */
    interface RefreshIndicator {

        /**
         * 표시된 새로 고침 표시기를 사용자에게 보이지 않게한다
         */
        void hide();

        /**
         * 사용자에게 새로 고침 표시기를 표시
         */
        void show();

        /**
         * 새로 고침 표시기가 사용자에게 표시되는지 여부를 나타냄
         *
         * @return Boolean 리프레시 표시기가 사용자에게 보여지는 지의 여부를 표시한다.
         */
        boolean isVisible();
    }
}
