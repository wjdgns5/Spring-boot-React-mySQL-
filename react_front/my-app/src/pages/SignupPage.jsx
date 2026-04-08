import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

function SignupPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const navigate = useNavigate();

  const handleSignup = async (e) => {
    e.preventDefault(); // 새로고침 차단
    try {
      await axios.post("http://localhost:8080/api/auth/signup", {
        username,
        password,
      });
      alert("회원가입 성공");
      navigate("/login"); // 로그인 페이지로 이동
    } catch (err) {
      alert("회원가입 실패 : " + (err.response?.data || err.message));
    }
  };

  return (
    <div className="auth-container">
      <h2>회원가입</h2>
      <form onSubmit={handleSignup}>
        <input
          type="text"
          placeholder="아이디"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        ></input>

        <input
          type="password"
          placeholder="비밀번호"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        ></input>

        <button type="submit">회원가입</button>
      </form>
      {/* 6분 50초 */}
    </div>
  );
} // end of SignupPage()

export default SignupPage;
