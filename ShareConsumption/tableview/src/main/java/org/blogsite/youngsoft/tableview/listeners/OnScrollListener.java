package org.blogsite.youngsoft.tableview.listeners;

import android.widget.AbsListView;
import android.widget.ListView;

/**
 * TableView의 데이터 뷰 (ListView)의 스크롤 및 스크롤 상태 변경을
 * 수신 대기하는 데 사용할 수있는 OnScrollListener 정의
  */
public interface OnScrollListener {

    /**
     * 기존의 스크롤 상태 컬렉션
     */
    enum ScrollState {

        /**
         * 이전에 터치를 사용하여 스크롤링을 수행하고 플링을 수행.
         */
        FLING(AbsListView.OnScrollListener.SCROLL_STATE_FLING),
        /**
         * 보기가 스크롤되지 않음
         */
        IDLE(AbsListView.OnScrollListener.SCROLL_STATE_IDLE),
        /**
         * 사용자가 터치를 사용하여 스크롤하고 있고 손가락이 화면에 계속 남아 있음
         */
        TOUCH_SCROLL(AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);

        private int value;

        ScrollState(final int value) {
            this.value = value;
        }

        /**
         * {@link AbsListView}에 정의되고있는 {@link android.widget.AbsListView.OnScrollListener}에 의해 알려진 값을 제공
         *
         * @return {@link AbsListView} 스크롤 상태값
         */
        public int getValue() {
            return value;
        }

        /**
         * {@link AbsListView} 스크롤 상태 값으로부터 {@link ScrollState}를 작성
         *
         * @param value {@link AbsListView} 스크롤 상태값
         * @return 작성된 ScrollState. 값이 무효 인 경우는 null
         */
        public static ScrollState fromValue(final int value) {
            switch (value) {
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    return IDLE;
                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    return TOUCH_SCROLL;
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    return FLING;
                default:
                    return null;
            }
        }
    }

    /**
     * Callback method to be invoked when the list or grid has been scrolled. This will be called after the scroll has completed.
     * 리스트 또는 그리드가 스크롤되었을 때 호출되는 콜백 메소드. 이것은 스크롤이 완료된 후에 호출됨
     *
     * @param tableDataView    스크롤 상태가보고 된 뷰
     * @param firstVisibleItem 최초의 가시 행의 인덱스입니다 (visibleItemCount == 0의 경우는 무시).
     * @param visibleItemCount 가시 행의 수.
     * @param totalItemCount   {@link org.blogsite.youngsoft.tableview.TableDataAdapter}에 있는 항목의 수
     */
    void onScroll(final ListView tableDataView, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount);

    /**
     * 테이블 데이터 뷰가 스크롤되는 동안 호출되는 콜백 메소드.
     * 뷰가 스크롤되고있는 경우,이 메서드는 스크롤의 다음의 프레임이 렌더링되기 전에 불려짐
     *
     * @param tableDateView 스크롤 상태가보고 된 뷰
     * @param scrollState   현재의 스크롤 상태입니다. {@link ScrollState#TOUCH_SCROLL} 또는 {@link ScrollState # IDLE} 중 하나
     */
    void onScrollStateChanged(final ListView tableDateView, final ScrollState scrollState);

}
