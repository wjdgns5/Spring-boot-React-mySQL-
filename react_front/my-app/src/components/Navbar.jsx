import { Link, useLocation } from "react-router-dom";

const Navbar = () => {
  const location = useLocation();
  return (
    <>
      <nav className="navbar">
        <div className="navbar-logo">MyApp</div>
        <div className="navbar-links">
          <Link className={location.pathname === "/" ? "active" : ""} to="/">
            홈
          </Link>

          <Link
            className={location.pathname === "/login" ? "active" : ""}
            to="/login"
          >
            로그인
          </Link>

          <Link
            className={location.pathname === "/signup" ? "active" : ""}
            to="/signup"
          >
            회원가입
          </Link>

          <Link
            className={location.pathname === "/admin" ? "active" : ""}
            to="/admin"
          >
            관리자
          </Link>
        </div>
      </nav>
    </>
  );
};

export default Navbar;
