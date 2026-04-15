import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import SignupPage from "./pages/SignupPage";
import LoginPage from "./pages/LoginPage";
import AdminPage from "./pages/AdminPage";
import "./css/App.css";
import Navbar from "./components/Navbar";
import { useDispatch } from "react-redux";
import { useEffect } from "react";
import { setUserFromToken } from "./store/userSlice";
import Cookies from "js-cookie";
import OAuthRedirectKakaoPage from "./pages/OAuthRedirectPage";

function App() {
  // Redux store에 액션(action)을 보내기 위한 함수
  // action 이란? “이렇게 바꿔라” 라는 명령서
  const dispatch = useDispatch();

  useEffect(() => {
    const token = Cookies.get("accessToken");
    if (token) {
      dispatch(setUserFromToken(token));
    }
  }, [dispatch]); // App 컴포넌트가 처음 실행될 때 로그인 상태를 복원하는 효과

  return (
    <Router>
      {/* BrowserRouter : 전체 길찾기 관리자 */}
      <Navbar />
      <div style={{ paddingTop: "80px" }}>
        <Routes>
          {/* Routes : 경로 목록 묶음 */}
          <Route path="/" element={<Home />}></Route>
          {/* Route : 개별 경로 설정 */}
          <Route path="/signup" element={<SignupPage />}></Route>
          <Route path="/login" element={<LoginPage />}></Route>
          <Route path="/admin" element={<AdminPage />}></Route>
          <Route
            path="/oauth2/redirect/kakao"
            element={<OAuthRedirectKakaoPage />}
          ></Route>
        </Routes>
      </div>
    </Router>
  );
}

export default App;
