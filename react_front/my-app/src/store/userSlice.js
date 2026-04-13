import { createSlice } from "@reduxjs/toolkit"; // Redux Toolkit에서 제공하는 함수
import { jwtDecode } from "jwt-decode"; // JWT 토큰 안에 들어있는 정보를 읽는 함수
import Cookies from "js-cookie"; // 브라우저 쿠키를 쉽게 다루는 라이브러리

// Redux에 처음 저장할 유저 관련 초기 상태(기본값)
const initialState = {
  username: null, // 로그인한 사용자 이름
  role: null, // 사용자 권한(USER, ADMIN 등)
  isAuthenticated: false, // 로그인 여부
};

// user 상태를 관리를 조각내서 관리하기 때문이다.
const userSlice = createSlice({
  name: "user", // slice 이름
  initialState, // 초기값
  reducers: {
    // 전달받은 JWT 토큰을 decode해서 사용자 정보를 state에 저장
    // reducers : 상태(state)를 변경하는 함수 모음
    setUserFromToken(state, action) {
      // state = 현재 Redux 안에 저장된 상태값
      // action = 이번에 전달된 명령 + 데이터
      const token = action.payload;
      try {
        const decode = jwtDecode(token); // Jwt토큰 내용 열기
        state.username = decode.sub;
        state.role = decode.role;
        state.isAuthenticated = true;
      } catch {
        ((state.username = null),
          (state.role = null),
          (state.isAuthenticated = false));
      }
    },

    logout(state) {
      Cookies.remove("accessToken");
      state.username = null;
      state.role = null;
      state.isAuthenticated = false;
    },
  },
});

// reducer 안에서 만든 액션 함수들을 export
export const { setUserFromToken, logout } = userSlice.actions;
// reducer를 store에 등록할 수 있도록 export
export default userSlice.reducer;
