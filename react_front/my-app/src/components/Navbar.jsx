import { Link, useLocation, useNavigate } from "react-router-dom";

// 전역 상태(Redux state)에서 내가 필요한 값만 꺼내오는 기능
import { useSelector } from "react-redux";

import axiosInstance from "../api/axiosInstance";
import Cookies from "js-cookie";
import { useDispatch } from "react-redux";
import { logout } from "../store/userSlice"; // Redux 에서 logout() 함수 불러옴

const Navbar = () => {
  const location = useLocation();
  const { role, isAuthenticated } = useSelector((state) => state.user);

  // Redux store에 액션(action)을 보내기 위한 함수
  // action 이란? “이렇게 바꿔라” 라는 명령서
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await axiosInstance.post("/api/auth/logout");
    } catch (err) {
      console.error("서버 로그아웃 실패: ", err);
    }
    Cookies.remove("accessToken");
    dispatch(logout()); // 로그아웃 함수 실행?
    navigate("/"); // 홈 페이지로 이동
  };

  return (
    <>
      <nav className="navbar">
        <div className="navbar-logo">MyApp</div>
        <div className="navbar-links">
          <Link className={location.pathname === "/" ? "active" : ""} to="/">
            홈
          </Link>
          {isAuthenticated ? (
            <>
              {role === "ROLE_ADMIN" && (
                <Link
                  className={location.pathname === "/admin" ? "active" : ""}
                  to="/admin"
                >
                  {" "}
                  관리자{" "}
                </Link>
              )}
              <Link onClick={handleLogout}>로그아웃</Link>
            </>
          ) : (
            <>
              <Link
                className={location.pathname === "/login" ? "active" : ""}
                to="/login"
              >
                {" "}
                로그인
              </Link>
              <Link
                className={location.pathname === "/signup" ? "active" : ""}
                to="/signup"
              >
                {" "}
                회원가입{" "}
              </Link>
            </>
          )}
        </div>
      </nav>
    </>
  );
};

export default Navbar;
