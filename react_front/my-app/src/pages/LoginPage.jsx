import axios from "axios";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Cookies from "js-cookie"; // Cookie
import { jwtDecode } from "jwt-decode"; // jwt-decode
import { setUserFromToken } from "../store/userSlice";
import { useDispatch } from "react-redux";

function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  // Redux store에 액션(action)을 보내기 위한 함수
  // action 이란? “이렇게 바꿔라” 라는 명령서
  const dispatch = useDispatch();

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

      console.log(jwtDecode(res.data.accessToken)); // 등급 표시
      Cookies.set("accessToken", res.data.accessToken, {
        expires: 0.021, // 0.021 × 24시간 = 0.504시간 (30분)
        path: "/",
      }); // 쿠키에 accessToken 값을 저장(set)

      // dispatch() : Redux에게 "이 액션 실행해" 라고 전달하는 것.
      dispatch(setUserFromToken(res.data.accessToken));

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
