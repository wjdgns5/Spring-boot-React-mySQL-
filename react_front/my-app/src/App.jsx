import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import SignupPage from "./pages/SignupPage";
import LoginPage from "./pages/LoginPage";
import AdminPage from "./pages/AdminPage";
import "./css/App.css";
import Navbar from "./components/Navbar";

function App() {
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
        </Routes>
      </div>
    </Router>
  );
}

export default App;
