import { Link, useLocation } from "react-router-dom";

// 전역 상태(Redux state)에서 내가 필요한 값만 꺼내오는 기능
import { useSelector } from "react-redux";

const Navbar = () => {
  const location = useLocation();
  const { role, isAuthenticated } = useSelector((state) => state.user);
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
