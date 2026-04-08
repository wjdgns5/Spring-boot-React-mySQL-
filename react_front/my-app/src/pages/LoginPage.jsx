import axios from "axios";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post(
        "http://localhost:8080/api/auth/login",
        {
          username,
          password,
        },
        { withCredentials: true },
        // withCredentials : (AJAX 요청) 도메인이 다를 때도 쿠키를 공유하여 로그인 상태를 유지해야 할 때 필수
      );
      alert("로그인 성공! Access Token : " + res.data.accessToken);
      navigate("/"); // home 페이지로 이동
    } catch (err) {
      alert("로그인 실패 : " + (err.response?.data || err.message));
    }
  };

  return (
    <div className="auth-container">
      <h2>로그인</h2>
      <form onSubmit={handleLogin}>
        <input
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          placeholder="아이디"
        ></input>

        <input
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="비밀번호"
        ></input>

        <button type="submit">로그인</button>
      </form>
    </div>
  );
}

export default LoginPage;
