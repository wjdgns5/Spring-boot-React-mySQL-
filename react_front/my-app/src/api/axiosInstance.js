// 자동적으로 Axios 요청을 할 때, Header에 쿠키를 담아서 보내도록 한다.
import axios from "axios";
import Cookies from "js-cookie";

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true, // Http only 설정 쿠키 같은 인증 정보(credentials)를 같이 보내도록 허용
});

// interceptors : 요청이 서버로 나가기 직전에 중간에서 가로채서 처리하는 기능
axiosInstance.interceptors.request.use(
  (config) => {
    // config : 이번에 보내려는 요청 정보 (url, method, headers, data, params) 묶음
    const token = Cookies.get("accessToken");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      // 엑세스 토큰 앞에 Bearer를 붙여야 한다. (Authorization: Bearer 토큰값)
    }
    return config;
  },
  (error) => Promise.reject(error),
);

export default axiosInstance;
