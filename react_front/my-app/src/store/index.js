// configureStore : Redux store를 쉽게 만들어주는 함수
import { configureStore } from "@reduxjs/toolkit";
// userSlice.js 에서 export default 한 reducer를 가져오는 거
import userReducer from "./userSlice";

// configureStore({...}) : Redux 전역 저장소(store) 생성
export const store = configureStore({
  // reducer: { ... } : 어떤 상태들을 어떤 reducer가 관리할지 적는다.
  // 즉, Redux store 안에 들어갈 상태의 구조를 정하는 부분
  reducer: {
    user: userReducer,
  },
});
